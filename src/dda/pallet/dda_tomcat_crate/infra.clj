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

(ns dda.pallet.dda-tomcat-crate.infra
  (:require
    [schema.core :as s]
    [pallet.api :as api]
    [dda.pallet.core.dda-crate :as dda-crate]
    [dda.config.commons.map-utils :as map-utils]
    [dda.pallet.dda-tomcat-crate.infra.schema :as schema]
    [dda.pallet.dda-tomcat-crate.infra.app :as app]
    [dda.pallet.dda-tomcat-crate.infra.app-config :as app-config]))

(def facility :dda-tomcat)

(def ServerXmlConfig schema/ServerXmlConfig)

(def JavaVmConfig schema/JavaVmConfig)

(def CustomConfig schema/CustomConfig)

(def TomcatConfig
  schema/TomcatConfig)

(def InternalConfig
  schema/TomcatConfig)

(def InfraResult {facility TomcatConfig})

(s/defn ^:always-validate merge-with-internal-config :- schema/TomcatInternalConfig
  [config :- TomcatConfig]
  (let [{:keys [java-vm-config]} config]
    (merge
      config
      {:default-lines (app-config/default-tomcat7 java-vm-config)
       :setenv-sh-lines (app-config/setenv-sh java-vm-config)})))

(s/defn ^:always-validate install
  "install function for httpd-crate."
  [config :- TomcatConfig]
  (app/install-tomcat7 (merge-with-internal-config config)))

(s/defn ^:always-validate configure
  "configure function for httpd-crate."
  [config :- TomcatConfig]
  (app/configure-tomcat7 (merge-with-internal-config config)))

(defmethod dda-crate/dda-install
  facility [dda-crate config]
  (install config))

(defmethod dda-crate/dda-configure
  facility [dda-crate config]
  (configure config))

(def dda-tomcat-crate
  (dda-crate/make-dda-crate
    :facility facility
    :version [0 1 0]
    :config-schema TomcatConfig
    :config-default {}))

(def with-tomcat
  (dda-crate/create-server-spec dda-tomcat-crate))
