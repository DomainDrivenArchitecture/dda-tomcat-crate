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
(ns dda.pallet.dda-tomcat-crate.infra.server-xml
   (:require
     [clojure.string :as string]
     [schema.core :as s]
     [selmer.parser :as selmer]
     [pallet.actions :as actions]))

(def ServerXmlConfig
  {:tomcat-version (s/enum 7 8)
   :config-server-xml-location s/Str
   :os-user s/Str
   :shutdown-port s/Str
   :start-ssl s/Bool
   :executor-daemon s/Str
   :executor-max-threads s/Str
   :executor-min-spare-threads s/Str
   :service-name s/Str
   :connector-port s/Str
   :connector-protocol (s/pred #(contains? #{"HTTP/1.1" "AJP/1.3"} %))
   :connection-timeout s/Str
   (s/optional-key :uri-encoding) s/Str})

(s/defn
  server-xml :- [s/Str]
  "the server-xml generator function."
  [config :- ServerXmlConfig]
  (let [template-file (cond
                          (= 7 (:tomcat-version config)) "etc_tomcat7_server.xml.template"
                          :else "etc_tomcat8_server.xml.template")]
    (string/split
      (selmer/render-file template-file
                          (merge
                            config
                            {:contains-uri-encode? (contains? config :uri-encoding)}))
      #"\n")))

(s/defn configure-server-xml
  [config :- ServerXmlConfig]
  (let [{:keys [config-server-xml-location os-user]} config]
    (actions/remote-file
      config-server-xml-location
      :owner os-user
      :group os-user
      :mode "644"
      :literal true
      :content (string/join
                 \newline
                 (server-xml config)))))
