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

(ns dda.pallet.dda-tomcat-crate.domain
  (:require
   [schema.core :as s]
   [dda.pallet.dda-tomcat-crate.infra :as infra]))

;TODO adjust and make a real domain configuration
(def DomainConfig
  "Represents the tomcat configuration."
  {})

  ;{:server-xml-config server-xml-config
  ; :java-vm-config java-vm-config
  ; :custom-config custom-config
  ; :os-package os-package
  ; :tomcat-home-location tomcat-home
  ; :config-base-location config-base
  ; :webapps-location (str tomcat-home "webapps/")
  ; :custom-bin-location custom-tomcat-bin
  ; :config-default-location "/etc/default/tomcat7"
  ; :config-server-xml-location (str config-base "server.xml")
  ; :config-catalina-properties-location (str config-base "catalina.properties")
  ; :config-setenv-sh-location (str custom-tomcat-bin "setenv.sh")
  ; :webapps-root-xml-location (str config-base "Catalina/localhost/ROOT.xml")
  ; :java-package java-package
  ; :download-url "http://apache.openmirror.de/tomcat/tomcat-7/v7.0.68/bin/apache-tomcat-7.0.68.tar.gz"})


(s/defn ^:always-validate
  infra-configuration :- infra/InfraResult
  [domain-config :- DomainConfig]
  ;(let [{:keys [server-xml-config java-vm-config custom-config]}]
  ;       :or {server-xml-config default-server-xml-config
  ;            java-vm-config default-heap-config
  ;            custom-config default-custom-config domain-config
  ;      os-package (not (contains? custom-config :custom-tomcat-home))
  ;      tomcat-home (if os-package
  ;                    "/var/lib/tomcat7/"
  ;                    (get-in custom-config [:custom-tomcat-home])
  ;      config-base (if os-package
  ;                    "/etc/tomcat7/"
  ;                    (str (get-in custom-config [:custom-tomcat-home]) "conf/")
  ;      custom-tomcat-bin (if os-package
  ;                          "/usr/share/tomcat7/bin/"
  ;                          (str tomcat-home "bin/")
  ;      java-package (str "openjdk-" (:jdk java-vm-config) "-jdk")
  (let [tomcat-managed? true
        config-base (if tomcat-managed?
                      "/etc/tomcat7/"
                      "/some/custom/tomcat-home/conf/")]
   {infra/facility
    {:server-xml
      {:shutdown-port "8005"
       :start-ssl false
       :executor-daemon "true"
       :executor-min-spare-threads "4"
       :executor-max-threads "151"
       :service-name "Catalina"
       :connector-port "8080"
       :connector-protocol "HTTP/1.1"
       :connection-timeout "61000"
       :uri-encoding "UTF-8"
       :config-server-xml-location (str config-base "server.xml")
       :os-user "tomcat7"}
     :tomct-vm
      {:managed {:config-default-location "/etc/default/tomcat7"}
       :settings #{}
       :xmx "512m"
       :xms "512m"
       :max-perm-size "128m"
       :os-user "tomcat7"
       :java-home "/usr/lib/jvm/java-1.7.0-openjdk-amd64"}
     :java
      {:java-version 8}
     :tomcat-source
      {:tomcat-managed {:package-name "tomcat7"}}}}))
