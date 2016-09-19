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

(ns org.domaindrivenarchitecture.pallet.crate.tomcat.schema
  (:require
    [schema.core :as s]
    [org.domaindrivenarchitecture.config.commons.directory-model :as dir-model]
    ))

(def ServerXmlConfig
  "The configuration needed for the server-xml file"
  {:shutdown-port s/Str
   :start-ssl s/Bool
   :executor-daemon s/Str
   :executor-max-threads s/Str
   :executor-min-spare-threads s/Str
   :service-name s/Str
   :connector-port s/Str
   :connector-protocol (s/pred #(contains? #{"HTTP/1.1" "AJP/1.3"} %)) 
   :connection-timeout s/Str
   (s/optional-key :uri-encoding) s/Str})

(def JavaVmConfig
  "The configuration of the heap settings"
  {:xms s/Str
   :xmx s/Str
   :max-perm-size s/Str
   :jdk6 s/Bool}
  )

(def CustomConfig
  {(s/optional-key :custom-tomcat-home) dir-model/NonRootDirectory
   :with-manager-webapps s/Bool})

(def TomcatConfig
  "The configuration for tomcat crate." 
  {:server-xml-config ServerXmlConfig
   :java-vm-config JavaVmConfig
   :custom-config CustomConfig
   :os-package s/Bool
   :tomcat-home-location dir-model/NonRootDirectory
   :config-base-location dir-model/NonRootDirectory
   :custom-bin-location dir-model/NonRootDirectory
   :webapps-location dir-model/NonRootDirectory
   :config-default-location s/Str
   :config-server-xml-location s/Str
   :config-setenv-sh-location s/Str
   :config-catalina-properties-location s/Str
   :webapps-root-xml-location s/Str
   :java-package s/Str
   :download-url s/Str
   :default-lines [s/Str]
   :setenv-sh-lines [s/Str]
   (s/optional-key :catalina-properties-lines) [s/Str]
   (s/optional-key :root-xml-lines) [s/Str]
   })
