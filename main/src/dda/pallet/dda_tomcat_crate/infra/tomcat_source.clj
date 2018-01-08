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
(ns dda.pallet.dda-tomcat-crate.infra.tomcat-source
  (:require
    [schema.core :as s]
    [pallet.actions :as actions]
    [dda.config.commons.directory-model :as dir-model]))

(def TomcatManaged
  {:tomcat-managed
   {:pacakge-name s/Str}})

(def TomcatDownload
  {:tomcat-download
   {:os-user s/Str
    :tomcat-home-location dir-model/NonRootDirectory
    :custom-bin-location dir-model/NonRootDirectory
    :download-url s/Str}})

(def TomcatSource
  (s/either
    TomcatManaged
    TomcatDownload))

(s/defn install-tomcat-download
  [config :- TomcatDownload]
  (let [{:keys [tomcat-home-location custom-bin-location download-url os-user]} config]
   ;download & unzip
   (actions/remote-directory
     tomcat-home-location
     :action :create
     :url download-url
     :strip-components 1                                     ;Note: strip-component only works with tar, not with unzip
     :unpack :tar
     :owner os-user
     :group os-user
     :mode "755")
   ; make shells executable
   (doseq [file ["catalina.sh" "configtest.sh" "daemon.sh" "digest.sh"
                 "setclasspath.sh" "shutdown.sh" "startup.sh" "tool-wrapper.sh"]]
     (actions/file
        (str custom-bin-location "/" file)
        :action :touch
        :mode 755))))

(s/defn install-tomcat-managed
  [config :- TomcatManaged]
  (actions/package (:package-name config)))

(s/defn install-tomcat
  [config :- TomcatSource]
  (let [{:keys [tomcat-managed tomcat-download]} config]
    (actions/package "unzip")
    (when (contains? config :tomcat-managed)
      (install-tomcat-managed tomcat-managed))
    (when (contains? config :tomcat-download)
      (install-tomcat-download tomcat-download))))
