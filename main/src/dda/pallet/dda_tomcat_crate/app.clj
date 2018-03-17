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
    [dda.pallet.core.app :as core-app]
    [dda.pallet.dda-config-crate.infra :as config-crate]
    [dda.pallet.dda-tomcat-crate.infra :as infra]
    [dda.pallet.dda-tomcat-crate.domain :as domain]))


(def with-tomcat infra/with-tomcat)

(def InfraResult infra/InfraResult)

(def DomainConfig domain/DomainConfig)

(def AppConfig
  {:group-specific-config
   {s/Keyword InfraResult}})

(s/defn ^:always-validate
  app-configuration :- AppConfig
  [domain-config :- DomainConfig
   & options]
 (let [{:keys [group-key] :or {group-key infra/facility}} options]
  {:group-specific-config
     {group-key (domain/infra-configuration domain-config)}}))

(s/defmethod ^:always-validate
  core-app/group-spec infra/facility
  [crate-app
   domain-config :- DomainConfig]
  (let [app-config (app-configuration domain-config)]
    (core-app/pallet-group-spec
      app-config [(config-crate/with-config app-config)
                  with-tomcat])))

(def crate-app (core-app/make-dda-crate-app
                  :facility infra/facility
                  :domain-schema DomainConfig
                  :domain-schema-resolved DomainConfig
                  :default-domain-file "tomcat.edn"))
