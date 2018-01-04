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
(ns dda.pallet.dda-tomcat-crate.infra.schema
  (:require
    [schema.core :as s]
    [dda.config.commons.directory-model :as dir-model]
    [dda.pallet.dda-tomcat-crate.infra.server-xml :as server-xml]))

(def ServerXmlConfig server-xml/ServerXmlConfig)

(def JavaVmConfig
  {:java-version s/Num
   :java-package s/Str
   :download-url s/Str
   :xms s/Str
   :xmx s/Str
   :max-perm-size s/Str})

(def TomcatLocations
  {:tomcat-home-location dir-model/NonRootDirectory
   :config-base-location dir-model/NonRootDirectory
   :custom-bin-location dir-model/NonRootDirectory
   :webapps-location dir-model/NonRootDirectory
   :config-default-location s/Str
   :config-server-xml-location s/Str
   :config-setenv-sh-location s/Str
   :config-catalina-properties-location s/Str
   :webapps-root-xml-location s/Str})

(def TomcatConfig
  "The configuration for tomcat crate."
  {:tomcat-version s/Num
   :java-vm-config JavaVmConfig
   :tomcat-locations TomcatLocations
   :server-xml-config ServerXmlConfig
   :setenv-sh-lines [s/Str]
   :catalina-properties-lines [s/Str]
   :root-xml-lines [s/Str]
   :download-url s/Str
   :settings (hash-set (s/enum :remove-manager-webapps))})

; deprecated
(defn get-tomcat-version
  [config]
  (-> config :tomcat-version))

(defn get-java-version
  [config]
  (-> config :java-version))

(s/defn get-xms :- s/Str
  [config :- TomcatConfig]
  (-> config :java-vm-config :xms))

(s/defn get-xmx :- s/Str
  [config :- TomcatConfig]
  (-> config :java-vm-config :xmx))

(s/defn get-max-perm-size :- s/Str
  [config :- TomcatConfig]
  (-> config :java-vm-config :max-perm-size))

(s/defn get-shutdown-port :- s/Str
  [config :- TomcatConfig]
  (-> config :server-xml-config :shutdown-port))

(s/defn get-start-ssl :- s/Bool
  [config :- TomcatConfig]
  (-> config :server-xml-config :start-ssl))
