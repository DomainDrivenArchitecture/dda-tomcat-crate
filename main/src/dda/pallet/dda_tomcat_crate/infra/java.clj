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

(ns dda.pallet.dda-tomcat-crate.infra.java
   (:require
     [pallet.actions :as actions]
     [schema.core :as s]))

(def JavaConfig
  {:java-version s/Num
   (s/optional-key :download-url) s/Str})

;todo: consider custom installation
(s/defn install-java
  [config :- JavaConfig]
  (actions/package (str "openjdk-" (:java-version config) "-jdk")))

(s/defn java-home
  [config :- JavaConfig]
  (str "/usr/lib/jvm/java-1." (:java-version config) ".0-openjdk-amd64"))
