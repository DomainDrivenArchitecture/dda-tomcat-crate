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
    [pallet.actions :as actions]
    [dda.pallet.core.infra :as core-infra]
    [dda.pallet.dda-tomcat-crate.infra.java :as java]
    [dda.pallet.dda-tomcat-crate.infra.tomcat-source :as tomcat-source]
    [dda.pallet.dda-tomcat-crate.infra.tomcat-vm :as tomcat-vm]
    [dda.pallet.dda-tomcat-crate.infra.server-xml :as server-xml]
    [dda.pallet.dda-tomcat-crate.infra.root-xml :as root-xml]
    [dda.pallet.dda-tomcat-crate.infra.catalina-properties :as catalina-properties]
    [dda.pallet.dda-tomcat-crate.infra.catalina-policy :as catalina-policy]
    [dda.pallet.dda-tomcat-crate.infra.management-webapp :as mgm-webapp]))

(def facility :dda-tomcat)

(def TomcatConfig
  "The configuration for tomcat crate."
  {:java java/JavaConfig
   :tomcat-source tomcat-source/TomcatSource
   :tomct-vm tomcat-vm/TomcatVmConfig
   :server-xml server-xml/ServerXmlConfig
   (s/optional-key :remove-manager-webapps) mgm-webapp/ManagementWebapp
   (s/optional-key :catalina-properties) catalina-properties/CatalinaProperties
   (s/optional-key :catalina-policy) catalina-policy/CatalinaPolicy
   (s/optional-key :root-xml) root-xml/RootXml})

(def InfraResult {facility TomcatConfig})

(s/defn ^:always-validate
  install
  "install function for httpd-crate."
  [config :- TomcatConfig]
  (let [{:keys [java tomcat-source remove-manager-webapps]} config]
    (java/install-java java)
    (tomcat-source/install-tomcat tomcat-source)
    (when (contains? config :remove-manager-webapps)
      (mgm-webapp/remove-manager-webapps remove-manager-webapps))))

(s/defn ^:always-validate
  configure
  "configure function for httpd-crate."
  [config :- TomcatConfig]
  (let [{:keys [tomct-vm server-xml catalina-properties catalina-policy root-xml]} config]
    (server-xml/configure-server-xml server-xml)
    (tomcat-vm/configure-tomcat-vm tomct-vm)
    (when (contains? config :catalina-properties)
      (catalina-properties/configure-catalina-properties catalina-properties))
    (when (contains? config :catalina-policy)
      (catalina-policy/configure-catalina-policy catalina-policy))
    (when (contains? config :root-xml)
      (root-xml/root-xml root-xml))))

(s/defn  ^:always-validate init
  "init package management"
  [config :- TomcatConfig]
  (actions/package-manager :update))

(defmethod core-infra/dda-init
  facility [core-infra config]
  (init config))

(defmethod core-infra/dda-install
  facility [core-infra config]
  (install config))

(defmethod core-infra/dda-configure
  facility [core-infra config]
  (configure config))

(def dda-tomcat-crate
  (core-infra/make-dda-crate-infra
    :facility facility))

(def with-tomcat
  (core-infra/create-infra-plan dda-tomcat-crate))
