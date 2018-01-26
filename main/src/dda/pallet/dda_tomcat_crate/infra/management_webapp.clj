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

(ns dda.pallet.dda-tomcat-crate.infra.management-webapp
   (:require
     [schema.core :as s]
     [selmer.parser :as selmer]
     [pallet.actions :as actions]
     [dda.config.commons.directory-model :as dir-model]))

(def ManagementWebapp
  {:webapps-location dir-model/NonRootDirectory
   :os-user s/Str})

;TODO version depending
(defn var-lib-tomcat-webapps-ROOT-index-html []
  (selmer/render-file "index.html.template" {}))

(defn var-lib-tomcat-webapps-ROOT-META-INF-context-xml []
  (selmer/render-file "context.xml.template" {}))

(s/defn remove-manager-webapps
  [config :- ManagementWebapp]
  (let [{:keys [os-user webapps-location]} config]
    (doseq [dir ["docs" "examples" "host-manager" "manager" "ROOT"]]
      (let [dir-path (str webapps-location "/" dir)]
        (actions/directory
          dir-path
          :action :delete)))
    (actions/directory
      (str webapps-location "/ROOT")
      :action :create :owner os-user :group os-user :mode "755")
    (actions/directory
      (str webapps-location "/ROOT/META-INF")
      :action :create :owner os-user :group os-user :mode "755")
    (actions/remote-file
      (str webapps-location "/ROOT/index.html")
      :owner os-user :group os-user
      :mode "644" :literal true
      :content (var-lib-tomcat-webapps-ROOT-index-html))
    (actions/remote-file
      (str webapps-location "/ROOT/META-INF/context.xml")
      :owner os-user :group os-user
      :mode "644" :literal true
      :content (var-lib-tomcat-webapps-ROOT-META-INF-context-xml))))
