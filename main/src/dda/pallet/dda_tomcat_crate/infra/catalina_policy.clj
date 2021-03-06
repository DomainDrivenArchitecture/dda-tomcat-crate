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

(ns dda.pallet.dda-tomcat-crate.infra.catalina-policy
  (:require
    [clojure.string :as string]
    [schema.core :as s]
    [selmer.parser :as selmer]
    [pallet.actions :as actions]
    [dda.config.commons.directory-model :as dir-model]))

(def CatalinaPolicy
  {:catalina-policy-location s/Str
   :os-user s/Str})

(s/defn catalina-policy []
  (let [template-file "etc_tomcat_catalina.policy"]
    (string/split
      (selmer/render-file template-file {})
      #"\n")))

(s/defn configure-catalina-policy
  [config :- CatalinaPolicy]
  (let [{:keys [os-user catalina-policy-location]} config]
    (actions/remote-file
      catalina-policy-location
      :owner "root" :group os-user
      :mode "644" :literal true
      :content (string/join
                 \newline
                 (catalina-policy)))))
