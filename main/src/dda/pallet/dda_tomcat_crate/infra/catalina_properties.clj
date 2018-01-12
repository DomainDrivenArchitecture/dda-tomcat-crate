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

(ns dda.pallet.dda-tomcat-crate.infra.catalina-properties
  (:require
    [clojure.string :as string]
    [schema.core :as s]
    [selmer.parser :as selmer]
    [pallet.actions :as actions]
    [dda.config.commons.directory-model :as dir-model]))

(def CatalinaProperties
  {:tomcat-version (s/enum 7 8)
   :config-catalina-properties-location s/Str
   :os-user s/Str
   :common-loader s/Str})

(s/defn catalina-properties
  [config :- CatalinaProperties]
  (let [template-file (cond
                          (= 7 (:tomcat-version config)) "etc_tomcat7_catalina.properties.template"
                          :else "tc_tomcat8_catalina.properties.template")]
    (string/split
      (selmer/render-file template-file config)
      #"\n")))

(s/defn configure-catalina-properties
  [config :- CatalinaProperties]
  (let [{:keys [os-user config-catalina-properties-location lines]} config]
    (actions/remote-file
      config-catalina-properties-location
      :owner os-user :group os-user
      :mode "644" :literal true
      :content (string/join
                 \newline
                 (catalina-properties config)))))
