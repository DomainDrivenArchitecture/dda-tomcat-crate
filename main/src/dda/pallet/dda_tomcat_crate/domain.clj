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
   ;(s/optional-key :server-xml-config) infra/ServerXmlConfig
   ;(s/optional-key :java-vm-config) infra/TomcatVmConfig})
  ; (s/optional-key :custom-config)  infra/CustomConfig})

(def default-server-xml-config
  "The default configuration needed for the server-xml file"
  {:shutdown-port "8005"
   :start-ssl false
   :executor-daemon "true"
   :executor-min-spare-threads "4"
   :executor-max-threads "151"
   :service-name "Catalina"
   :connector-port "8080"
   :connector-protocol "HTTP/1.1"
   :connection-timeout "61000"
   :uri-encoding "UTF-8"})

(def default-heap-config
  "The default configuration of the heap settings"
  {:xms "512m"
   :xmx "512m"
   :max-perm-size "128m"
   :jdk 8})

;(s/defn ^:always-validate merge-with-internal-config :- schema/TomcatInternalConfig
;  [config :- TomcatConfig]
;  (let [{:keys [java-vm-config]} config]
;    (merge
;      config
;      {:default-lines (app-config/default-tomcat7 java-vm-config)
;       :setenv-sh-lines (app-config/setenv-sh java-vm-config))

(def default-custom-config
  {:remove-manager-webapps true})

(s/defn ^:always-validate infra-configuration :- infra/InfraResult
  [domain-config :- DomainConfig]
  (let [{:keys [server-xml-config java-vm-config custom-config]
         :or {server-xml-config default-server-xml-config
              java-vm-config default-heap-config
              custom-config default-custom-config}} domain-config
        os-package (not (contains? custom-config :custom-tomcat-home))
        tomcat-home (if os-package
                      "/var/lib/tomcat7/"
                      (get-in custom-config [:custom-tomcat-home]))
        config-base (if os-package
                      "/etc/tomcat7/"
                      (str (get-in custom-config [:custom-tomcat-home]) "conf/"))
        custom-tomcat-bin (if os-package
                            "/usr/share/tomcat7/bin/"
                            (str tomcat-home "bin/"))
        java-package (str "openjdk-" (:jdk java-vm-config) "-jdk")]
    {infra/facility
     {:server-xml-config server-xml-config
      :java-vm-config java-vm-config
      :custom-config custom-config
      :os-package os-package
      :tomcat-home-location tomcat-home
      :config-base-location config-base
      :webapps-location (str tomcat-home "webapps/")
      :custom-bin-location custom-tomcat-bin
      :config-default-location "/etc/default/tomcat7"
      :config-server-xml-location (str config-base "server.xml")
      :config-catalina-properties-location (str config-base "catalina.properties")
      :config-setenv-sh-location (str custom-tomcat-bin "setenv.sh")
      :webapps-root-xml-location (str config-base "Catalina/localhost/ROOT.xml")
      :java-package java-package
      :download-url "http://apache.openmirror.de/tomcat/tomcat-7/v7.0.68/bin/apache-tomcat-7.0.68.tar.gz"}}))
