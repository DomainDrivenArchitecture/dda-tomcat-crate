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

(ns dda.pallet.dda-tomcat-crate.infra.root-xml
  (:require
    [schema.core :as s]
    [clojure.string :as string]
    [pallet.actions :as actions]
    [dda.config.commons.directory-model :as dir-model]))

(def RootXml
  {:webapps-root-xml-location dir-model/NonRootDirectory
   :os-user s/Str
   :lines [s/Str]})

(s/defn root-xml
  [config :- RootXml]
  (let [{:keys [os-user webapps-root-xml-location lines]} config]
    (actions/remote-file
      webapps-root-xml-location
      :owner os-user :group os-user
      :mode "644" :literal true
      :content (string/join
                 \newline
                 lines))))
