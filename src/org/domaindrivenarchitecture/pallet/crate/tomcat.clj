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

(ns org.domaindrivenarchitecture.pallet.crate.tomcat 
  (:require
    [schema.core :as s]
    [schema-tools.core :as st]
    [pallet.actions :as actions]
    [pallet.api :as api]
    [org.domaindrivenarchitecture.config.commons.map-utils :as map-utils]
    [org.domaindrivenarchitecture.pallet.crate.config-0-3 :as config]
    [org.domaindrivenarchitecture.config.commons.directory-model :as dir-model]
    [org.domaindrivenarchitecture.pallet.crate.tomcat.app :as tomcat-app]
    [org.domaindrivenarchitecture.pallet.crate.tomcat.app-config :as app-config]
    ))

(def TomcatConfig
  "The configuration for tomcat crate." 
  {:xmx s/Str
   :xms s/Str
   :max-perm-size s/Str
   :home-dir dir-model/NonRootDirectory
   :webapps-dir dir-model/NonRootDirectory
   :server-xml-config app-config/ServerXmlConfig
   :java-vm-config app-config/JavaVmConfig
   :custom-config app-config/CustomConfig
   })

(def tomcatDefaultConfig
  "Tomcat Crate Default Configuration"
  {:xmx "1024m"
   :xms "256m"
   :max-perm-size "512m"
   :home-dir "/var/lib/tomcat7/"
   :webapps-dir "/var/lib/tomcat7/webapps/"
   :server-xml-config app-config/default-server-xml-config
   :java-vm-config app-config/default-heap-config
   :custom-config app-config/default-custom-config})

(def ^:dynamic with-tomcat
  (api/server-spec
    :phases 
    {:install
     (api/plan-fn
       (tomcat-app/install-tomcat7)
       )
    }))

(s/defn ^:always-validate merge-config :- TomcatConfig
  "merges the partial config with default config & ensures that resulting config is valid."
  [partial-config]
  (map-utils/deep-merge tomcatDefaultConfig partial-config))