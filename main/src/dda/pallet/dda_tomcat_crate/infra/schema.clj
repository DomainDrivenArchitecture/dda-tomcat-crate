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
    [dda.pallet.dda-tomcat-crate.infra.server-xml :as server-xml]
    [dda.pallet.dda-tomcat-crate.infra.tomcat-vm :as tomcat-vm]
    [dda.pallet.dda-tomcat-crate.infra.management-webapp :as mgm-webapp]
    [dda.pallet.dda-tomcat-crate.infra.java :as java]
    [dda.pallet.dda-tomcat-crate.infra.catalina-properties :as catalina-properties]
    [dda.pallet.dda-tomcat-crate.infra.root-xml :as root-xml]))

(def ServerXmlConfig server-xml/ServerXmlConfig)

(def JavaConfig java/JavaConfig)

(def TomcatVmConfig tomcat-vm/TomcatVmConfig)

(def TomcatLocations
  {:tomcat-home-location dir-model/NonRootDirectory
   :config-base-location dir-model/NonRootDirectory
   :custom-bin-location dir-model/NonRootDirectory})

(def TomcatConfig
  "The configuration for tomcat crate."
  {:java JavaConfig
   :tomct-vm TomcatVmConfig
   :server-xml-config ServerXmlConfig
   (s/optional-key :remove-manager-webapps) mgm-webapp/ManagementWebapp
   (s/optional-key :catalina-properties) catalina-properties/CatalinaProperties
   (s/optional-key :root-xml) root-xml/RootXml})
