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
   [dda.pallet.dda-tomcat-crate.infra :as infra]
   [dda.config.commons.directory-model :as dir-model]))

(def LR6
  {:lr-6x {:xmx-megabbyte s/Num                   ; e.g. 6072 or 2560
           :lr-home dir-model/NonRootDirectory}}) ; e.g. /var/lib/liferay

(def LR7
  {:lr-7x {}})

(def DomainConfig
  "Represents the tomcat for liferay configuration."
  (s/either
    LR6
    LR7))

(s/defn
  lr-6x-infra-configuration :- infra/InfraResult
  [domain-config :- LR6]
  (let [{:keys [xmx-megabbyte lr-home]} domain-config]
   {infra/facility
    {:server-xml
      {:shutdown-port "8005"
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
       :os-user "tomcat7"}
     :tomct-vm
      {:managed {:config-default-location "/etc/default/tomcat7"}
       :settings #{:prefer-ipv4 :disable-cl-clear-ref
                   :conc-mark-sweep-gc :timezone-gmt
                   :disable-tomcat-security}
       :xmx (str xmx-megabbyte "m")
       :xms "1536m"
       :max-perm-size "512m"
       :os-user "tomcat7"
       :java-home "/usr/lib/jvm/java-1.8.0-openjdk-amd64"
       :catalina-opts (str "-Dcustom.lr.dir=" lr-home)}
     ;there is no longer support for java6 & java7, you've to install manually:
     ;https://askubuntu.com/questions/67909/how-do-i-install-oracle-jdk-6
     :java
      {:java-version 8}
     :tomcat-source
      {:tomcat-managed {:package-name "tomcat7"}}}}))

(s/defn
  infra-configuration :- infra/InfraResult
  [domain-config :- DomainConfig]
  (let [{:keys [lr-6x lr7x]} domain-config]
    (when (contains? domain-config :lr-6x)
      (lr-6x-infra-configuration lr-6x))))
