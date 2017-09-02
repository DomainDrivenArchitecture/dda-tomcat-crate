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
    [dda.pallet.dda-tomcat-crate.domain :as domain]))

(def InfraResult infra/InfraResult)

(def AppConfig
  {:group-specific-config
   {s/Keyword InfraResult}})

(def with-tomcat infra/with-tomcat)

(s/defn ^:allways-validate app-configuration :- AppConfig
 [domain-config :- domain/DomainConfig
  & options]
 (let [{:keys [group-key] :or {group-key :dda-tomcat-group}} options]
  {:group-specific-config
     {group-key (domain/infra-configuration domain-config)}}))

(s/defn ^:always-validate tomcat-group-spec
 [app-config :- AppConfig]
 (group/group-spec
   app-config [(config-crate/with-config app-config)
               with-tomcat]))
