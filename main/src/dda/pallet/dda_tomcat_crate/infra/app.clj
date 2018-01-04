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

(ns dda.pallet.dda-tomcat-crate.infra.app
  (:require
    [schema.core :as s]
    [clojure.string :as string]
    [pallet.actions :as actions]
    [pallet.stevedore :as stevedore]
    [dda.pallet.dda-tomcat-crate.infra.schema :as schema]
    [dda.pallet.dda-tomcat-crate.infra.app-config :as config]))

(defn- tomcat-package-name
  "Represents the tomcat package name. E.g. tomcat7"
  [config]
  (str "tomcat" (schema/get-tomcat-version config)))

(defn write-tomcat-file
  "Create and upload a config file"
  [file-name config & {:keys [content executable? overwrite-changes]
                             :or   {executable? false
                                    overwrite-changes true}}]
  (let [tomcat-user (str "tomcat" (schema/get-tomcat-version config))]
    (if (some? content)
      (actions/remote-file
        (str file-name)                                     ;makes sure this is a string -> assertion fail otherwise
        :owner tomcat-user
        :group tomcat-user
        :mode (if executable? "755" "644")
        :overwrite-changes overwrite-changes
        :literal true
        :content
        (string/join
          \newline
          content)))))

(defn create-tomcat-directory
  [directry-location config]
  (let [tomcat-user (str "tomcat" (schema/get-tomcat-version config))]
   (actions/directory
     directry-location
     :action :create
     :owner tomcat-user                                      ;assumes existing tomcat7 user
     :group tomcat-user
     :mode "755")))

(s/defn make-tomcat-executable
  [config :- schema/TomcatConfig]
  (doseq [file ["catalina.sh" "configtest.sh" "daemon.sh" "digest.sh"
                "setclasspath.sh" "shutdown.sh" "startup.sh" "tool-wrapper.sh"]]
    (let [file-path
          (str (get-in config [:custom-bin-location]) "/" file)]
      (actions/file
        file-path
        :action :touch
        :mode 755))))

(s/defn remove-manager-webapps
  [config :- schema/TomcatConfig]
  (let [webapps (get-in config [:webapps-location])]
    (doseq [dir ["docs" "examples" "host-manager" "manager" "ROOT"]]
      (let [dir-path (str webapps "/" dir)]
        (actions/directory
          dir-path
          :action :delete)))
    (create-tomcat-directory (str webapps "/ROOT") config)
    (create-tomcat-directory (str webapps "/ROOT/META-INF") config)
    (write-tomcat-file (str webapps "/ROOT/index.html") config
                       :overwrite-changes false
                       :executable? true
                       :content config/var-lib-tomcat7-webapps-ROOT-index-html)
    (write-tomcat-file (str webapps "/ROOT/META-INF/context.xml") config
                       :overwrite-changes false
                       :executable? true
                       :content config/var-lib-tomcat7-webapps-ROOT-META-INF-context-xml)))

(s/defn install-tomcat7-custom
  [config :- schema/TomcatConfig]
  (let [tomcat-user (tomcat-package-name config)]
   (actions/remote-directory
     (get-in config [:tomcat-home-location])
     :action :create
     :url (get-in config [:download-url])
     :strip-components 1                                     ;Note: strip-component only works with tar, not with unzip
     :unpack :tar
     :owner tomcat-user
     :group tomcat-user
     :mode "755")
   (make-tomcat-executable config)))

(defn install-openjdk
  [config]
  (actions/package (str "openjdk-" (schema/get-java-version config) "-jdk")))

(defn install-tomcat-package
  [config]
  (actions/package (tomcat-package-name config)))

(s/defn install-tomcat7
  [config :- schema/TomcatConfig]
  (actions/package "unzip")
  (install-openjdk config)
  (if (get-in config [:os-package])
    (install-tomcat-package config)
    (install-tomcat7-custom config))
  (when (get-in config [:remove-manager-webapps])
    (remove-manager-webapps (get-in config [:tomcat-home-location]))))

(s/defn configure-tomcat7
  [config :- schema/TomcatConfig]
  (write-tomcat-file
    (get-in config [:config-server-xml-location])
    :content (config/server-xml (get-in config [:server-xml-config])))
  (when (contains? config :catalina-properties-lines)
    (write-tomcat-file
      (get-in config [:config-catalina-properties-location])
      :content (get-in config [:catalina-properties-lines])))
  (when (contains? config :root-xml-lines)
    (write-tomcat-file
      (get-in config [:webapps-root-xml-location])
      :content (get-in config [:root-xml-lines])))
  (if (get-in config [:os-package])
    (write-tomcat-file
      (get-in config [:config-default-location])
      :content (get-in config [:default-lines]))
    (write-tomcat-file
      (get-in config [:config-setenv-sh-location])
      :content (get-in config [:setenv-sh-lines])
      :executable? true)))