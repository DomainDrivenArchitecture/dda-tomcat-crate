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

(ns org.domaindrivenarchitecture.pallet.crate.tomcat.app
   (:require
     [schema.core :as s]
     [clojure.string :as string]
     [pallet.actions :as actions]
     [pallet.stevedore :as stevedore]
     [org.domaindrivenarchitecture.pallet.crate.tomcat.schema :as schema]
     [org.domaindrivenarchitecture.pallet.crate.tomcat.app-config :as config]
    ))

(defn write-tomcat-file
  "Create and upload a config file"
  [file-name & {:keys [content executable?]
                :or {executable? false}}]
  (if (some? content)
    (actions/remote-file
      (str file-name) ;makes sure this is a string -> assertion fail otherwise
      :owner "tomcat7"
      :group "tomcat7"
      :mode (if executable? "755" "644")
      :overwrite-changes true
      :literal true
      :content 
      (string/join
        \newline
        content))))

(s/defn make-tomcat-executable
  [config :- schema/TomcatConfig]
  (doseq [file ["catalina.sh" "configtest.sh" "daemon.sh" "digest.sh"
                "setclasspath.sh" "shutdown.sh" "startup.sh" "tool-wrapper.sh"]]
    (let [file-path 
          (str (get-in config [:custom-bin-location]) "/" file)]
      (actions/file
        file-path
        :action :touch
        :mode 755)
      ))
  )

(s/defn remove-manager-webapps
  [config  :- schema/TomcatConfig]  
  (let [webapps (get-in config [:webapps-location])]
  (doseq [dir ["docs" "examples" "host-manager" "manager" "ROOT"]]
    (let [dir-path (str webapps "/" dir)]
      (actions/directory
        dir-path
        :action :delete)
      ))
  (actions/directory
    (str webapps "/ROOT")
    :action :create
    :owner "tomcat7"  ;assumes existing tomcat7 user
    :group "tomcat7"
    :mode "755"
    )
  (actions/directory
    (str webapps "/ROOT/META-INF")
    :action :create
    :owner "tomcat7"
    :group "tomcat7"
    :mode "755"
    )
  (actions/remote-file
    (str webapps "/ROOT/index.html")
    :owner "tomcat7"
    :group "tomcat7"
    :mode "755"
    :literal true
    :content 
    (string/join
      \newline
      config/var-lib-tomcat7-webapps-ROOT-index-html))
  (actions/remote-file
    (str webapps "/ROOT/META-INF/context.xml")
    :owner "tomcat7"
    :group "tomcat7"
    :mode "755"
    :literal true
    :content 
    (string/join
      \newline
      config/var-lib-tomcat7-webapps-ROOT-META-INF-context-xml))
  ))

(s/defn install-tomcat7-custom
  [config :- schema/TomcatConfig]
  (actions/remote-directory
    (get-in config [:tomcat-home])
    :action :create
    :url (get-in config [:download-url])
    :strip-components 1      ;Note: strip-component only works with tar, not with unzip
    :unpack :tar
    :owner "tomcat7"
    :group "tomcat7"
    :mode "755")
  (make-tomcat-executable config))

(s/defn install-tomcat7
  [config :- schema/TomcatConfig]
  (actions/package "unzip")
  (actions/package (get-in config [:java-package]))
  (if (get-in config [:os-package])
    (actions/package "tomcat7")
    (install-tomcat7-custom config))
  (when (not (get-in config [:with-manager-webapps]))
    (remove-manager-webapps (get-in config [:tomcat-home])))
  )

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
      :executable? true))
  )