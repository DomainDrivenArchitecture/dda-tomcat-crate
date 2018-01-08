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
(ns dda.pallet.dda-tomcat-crate.app
  (:require
    [schema.core :as s]
    [dda.cm.group :as group]
    [dda.pallet.dda-config-crate.infra :as config-crate]
    [dda.pallet.dda-tomcat-crate.infra :as infra]
    [dda.pallet.dda-tomcat-crate.domain :as domain]
    [dda.pallet.commons.existing :as existing]
    [dda.pallet.commons.external-config :as ext-config]))


(def with-tomcat infra/with-tomcat)

(def InfraResult infra/InfraResult)

(def DomainConfig domain/DomainConfig)

(def ProvisioningUser existing/ProvisioningUser)

(def Targets existing/Targets)

(def AppConfig
  {:group-specific-config
   {s/Keyword InfraResult}})

(s/defn ^:always-validate
  load-targets :- Targets
  [file-name :- s/Str]
  (existing/load-targets file-name))

(s/defn ^:always-validate
  load-domain :- DomainConfig
  [file-name :- s/Str]
  (ext-config/parse-config file-name))

(s/defn ^:always-validate
  app-configuration :- AppConfig
  [domain-config :- domain/DomainConfig & options]
 (let [{:keys [group-key] :or {group-key infra/facility}} options]
  {:group-specific-config
     {group-key (domain/infra-configuration domain-config)}}))

(s/defn ^:always-validate
  tomcat-group-spec
  [app-config :- AppConfig]
  (group/group-spec
    app-config [(config-crate/with-config app-config)
                with-tomcat]))

(s/defn ^:always-validate
  existing-provisioning-spec
  "Creates an integrated group spec from a domain config and a provisioning user."
  [domain-config :- domain/DomainConfig
   provisioning-user :- ProvisioningUser]
  (merge
    (tomcat-group-spec (app-configuration domain-config))
    (existing/node-spec provisioning-user)))
