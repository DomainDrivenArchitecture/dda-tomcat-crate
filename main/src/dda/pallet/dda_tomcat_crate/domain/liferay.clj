; Licensed to the Apache Software Foundation (ASF) under one
; or more contributor license agreements. See the NOTICE file
; distributed with this work for additional information
; regarding copyright ownership. The ASF licenses this file
; to you under the Apache License, Version 2.0 (the
; "License"); you may not use this file except in compliance
; with the License. You may obtain a copy of the License at
;
; http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.

(ns dda.pallet.dda-tomcat-crate.domain.liferay
  (:require
   [schema.core :as s]
   [clojure.string :as string]
   [selmer.parser :as selmer]
   [dda.pallet.dda-tomcat-crate.infra :as infra]
   [dda.config.commons.directory-model :as dir-model]
   [dda.pallet.dda-tomcat-crate.infra.catalina-properties :as catalina-properties]
   [dda.pallet.dda-tomcat-crate.infra.root-xml :as root-xml]))

; -------------------  schemas  ---------------------
(def LrCommon
  {:xmx-megabyte s/Num                   ; e.g. 6072 or 2560
   :lr-home dir-model/NonRootDirectory}) ; e.g. /var/lib/liferay

(def LR6
  {:lr-6x LrCommon})

(def LR7
  {:lr-7x LrCommon})

(def DomainConfig
  "Represents the tomcat for liferay configuration."
  (s/either
    LR6
    LR7))

; ----------------  fields and functions  -------------
(def os-user "tomcat7")
(def tomcat-home-dir "/etc/tomcat7/")

;this does not neet to be changed for LR7
(s/defn
  etc-tomcat-Catalina-localhost-ROOT-xml :- [s/Str]
  []
  (string/split
    (selmer/render-file "liferay_Root.xml.template" {})
    #"\n"))

(s/defn
  lr-7x-infra-configuration :- infra/InfraResult
  [domain-config :- LrCommon]
  (let [{:keys [xmx-megabyte lr-home]} domain-config]
   {infra/facility
    {:server-xml
      {:tomcat-version 8
       :shutdown-port "8005"
       :start-ssl true
       :executor-daemon "false"
       :executor-min-spare-threads "48"
       :executor-max-threads "151"
       :service-name "Catalina"
       :connector-port "8009"
       :connector-protocol "AJP/1.3"
       :connection-timeout "61000"
       :uri-encoding "UTF-8"
       :config-server-xml-location "/etc/tomcat8/server.xml"
       :os-user "tomcat8"}
     :tomct-vm
      {:managed {:config-default-location "/etc/default/tomcat8"}
       :tomcat-version 8
       :os-user "tomcat8"
       :java-home "/usr/lib/jvm/java-1.8.0-openjdk-amd64"
       :xms "1536m"
       :xmx (str xmx-megabyte "m")
       :max-perm-size "512m"
       :settings #{:prefer-ipv4 :disable-cl-clear-ref
                   :conc-mark-sweep-gc :timezone-gmt
                   :disable-tomcat-security}
       :catalina-opts (str "-Dcustom.lr.dir=" lr-home)}
     :java
      {:java-version 8}
     :tomcat-source
      {:tomcat-managed {:package-name "tomcat8"}}
     :catalina-properties {:tomcat-version 8
                           :os-user "tomcat8"
                           :config-catalina-properties-location "/etc/tomcat8/catalina.properties"
                           :common-loader ",\"/var/lib/liferay/lib/*.jar\""}
     :catalina-policy {:os-user "tomcat8"
                       :catalina-policy-location "/etc/tomcat8/catalina.policy"}
     :root-xml {:os-user "tomcat8"
                :webapps-root-xml-location "/etc/tomcat8/Catalina/localhost/ROOT.xml"
                :lines (etc-tomcat-Catalina-localhost-ROOT-xml)}}}))

(s/defn
  lr-6x-infra-configuration :- infra/InfraResult
  [domain-config :- LrCommon]
  (let [{:keys [xmx-megabyte lr-home]} domain-config]
   {infra/facility
    {:server-xml
      {:tomcat-version 7
       :shutdown-port "8005"
       :start-ssl true
       :executor-daemon "false"
       :executor-min-spare-threads "48"
       :executor-max-threads "151"
       :service-name "Catalina"
       :connector-port "8009"
       :connector-protocol "AJP/1.3"
       :connection-timeout "61000"
       :uri-encoding "UTF-8"
       :config-server-xml-location "/etc/tomcat7/server.xml"
       :os-user os-user}
     :tomct-vm
      {:tomcat-version 7
       :managed {:config-default-location "/etc/default/tomcat7"}
       :settings #{:prefer-ipv4 :disable-cl-clear-ref
                   :conc-mark-sweep-gc :timezone-gmt
                   :disable-tomcat-security}
       :xmx (str xmx-megabyte "m")
       :xms "1536m"
       :max-perm-size "512m"
       :os-user os-user
       :java-home "/usr/lib/jvm/java-1.8.0-openjdk-amd64"
       :catalina-opts (str "-Dcustom.lr.dir=" lr-home)}
     ;there is no longer support for java6 & java7, you've to install manually:
     ;https://askubuntu.com/questions/67909/how-do-i-install-oracle-jdk-6
     :java
      {:java-version 8}
     :tomcat-source
      {:tomcat-managed {:package-name "tomcat7"}}
     :catalina-properties {:tomcat-version 7
                           :os-user os-user
                           :config-catalina-properties-location "/etc/tomcat7/catalina.properties"
                           :common-loader ",/var/lib/liferay/lib/*.jar"}
     :root-xml {:os-user os-user
                :webapps-root-xml-location "/etc/tomcat7/Catalina/localhost/ROOT.xml"
                :lines (etc-tomcat-Catalina-localhost-ROOT-xml)}}}))
