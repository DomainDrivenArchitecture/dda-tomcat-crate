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

(ns dda.pallet.dda-tomcat-crate.domain.app-server
  (:require
   [schema.core :as s]
   [dda.pallet.dda-tomcat-crate.infra :as infra]))

(def AppServerConfig
  "Represents the tomcat configuration."
  {(s/optional-key :xmx-megabyte) s/Num})

(def DomainConfig
  "Represents the tomcat configuration."
  {:app-server AppServerConfig})

(s/defn
  infra-configuration :- infra/InfraResult
  [domain-config :- AppServerConfig]
  (let [{:keys [xmx-megabyte]
         :or {xmx-megabyte 512}} domain-config]
    {infra/facility
      {:server-xml
        {:tomcat-version 8
         :shutdown-port "8005"
         :start-ssl false
         :executor-daemon "true"
         :executor-min-spare-threads "4"
         :executor-max-threads "151"
         :service-name "Catalina"
         :connector-port "8009"
         :connector-protocol "AJP/1.3"
         :connection-timeout "61000"
         :uri-encoding "UTF-8"
         :config-server-xml-location "/etc/tomcat8/server.xml"
         :os-user "tomcat8"}
       :tomct-vm
        {:tomcat-version 8
         :managed {:config-default-location "/etc/default/tomcat8"}
         :settings #{}
         :xmx (str xmx-megabyte "m")
         :xms "512m"
         :max-perm-size "128m"
         :os-user "tomcat8"
         :java-home "/usr/lib/jvm/java-1.8.0-openjdk-amd64"}
       :java
        {:java-version 8}
       :tomcat-source
        {:tomcat-managed {:package-name "tomcat8"}}
       :remove-manager-webapps {:webapps-location "/var/lib/tomcat8/webapps/"
                                :os-user "tomcat8"}}}))
