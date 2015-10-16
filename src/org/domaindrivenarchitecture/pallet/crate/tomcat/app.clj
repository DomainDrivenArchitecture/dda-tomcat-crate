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
     [clojure.string :as string]
     [pallet.actions :as actions]
     [pallet.stevedore :as stevedore]
     [org.domaindrivenarchitecture.pallet.crate.tomcat.app-config :as config]
    ))

(defn- write-tomcat-file
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

(defn tomcat-config
  "Provides a map with all tomcat configurations. If parameter 
custom-home is provided, then a custom tomcat is installed. In 
other case the default ubuntu package is used."
  [& {:keys [custom-tomcat-home
             custom-java-version
             with-manager-webapps]
      :or {custom-tomcat-home nil
           custom-java-version :7
           with-manager-webapps false}}]
  (let [os-package (empty? custom-tomcat-home)
        tomcat-home (if os-package "/var/lib/tomcat7" custom-tomcat-home)
        config-base (if os-package "/etc/tomcat7" (str custom-tomcat-home "/conf"))
        custom-tomcat-bin (if os-package "/usr/share/tomcat7/bin" (str tomcat-home "/bin"))]
  {:os-package os-package
   :tomcat-home tomcat-home
   :config-default "/etc/default/tomcat7"
   :config-base config-base
   :config-server-xml (str config-base "/server.xml")
   :config-catalina-properties (str config-base "/catalina.properties")
   :config-setenv-sh (str custom-tomcat-bin "/setenv.sh")
   :custom-bin custom-tomcat-bin
   :webapps (str tomcat-home "/webapps")
   :webapps-root-xml (str config-base "/Catalina/localhost/ROOT.xml")
   :java-package (cond
                   (= custom-java-version :6) "openjdk-6-jdk"
                   :else "openjdk-7-jdk")
   :download-url "http://ftp.halifax.rwth-aachen.de/apache/tomcat/tomcat-7/v7.0.64/bin/apache-tomcat-7.0.64.tar.gz"
   :with-manager-webapps with-manager-webapps 
   }))

(defn- make-tomcat-executable
  [custom-tomcat-home]
  (doseq [file ["catalina.sh" "configtest.sh" "daemon.sh" "digest.sh"
                "setclasspath.sh" "shutdown.sh" "startup.sh" "tool-wrapper.sh"]]
    (let [file-path (str 
                      (:custom-bin (tomcat-config :custom-tomcat-home custom-tomcat-home)) 
                      "/" file)]
      (actions/file
        file-path
        :action :touch
        :mode 755)
      ))
  )

(defn- remove-manager-webapps
  [custom-tomcat-home]  
  (let [webapps (:webapps (tomcat-config :custom-tomcat-home custom-tomcat-home))]
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

(defn- install-tomcat7-custom
  [config]
  (actions/remote-directory
    (:tomcat-home config)
    :action :create
    :url (:download-url config)
    :strip-components 1      ;Note: strip-component only works with tar, not with unzip
    :unpack :tar
    :owner "tomcat7"
    :group "tomcat7"
    :mode "755")
  (make-tomcat-executable (:tomcat-home config))
  (if (not (:with-manager-webapps config))
    (remove-manager-webapps (:tomcat-home config)))
  )

(defn install-tomcat7
  [& {:keys [custom-tomcat-home
             custom-java-version
             with-manager-webapps]}]
  (let [config (tomcat-config 
                 :custom-tomcat-home custom-tomcat-home
                 :custom-java-version custom-java-version
                 :with-manager-webapps with-manager-webapps)]
    (actions/package (:java-package config))
    (actions/package "unzip")
    (if (:os-package config)
      (actions/package "tomcat7")
      (install-tomcat7-custom config)
    )))

(defn configure-tomcat7
  [& {:keys [custom-tomcat-home
             lines-etc-default-tomcat7 lines-server-xml lines-catalina-properties
             lines-ROOT-xml lines-setenv-sh]
      :or {lines-etc-default-tomcat7 (config/default-tomcat7)
           lines-server-xml (config/server-xml)
           lines-catalina-properties nil 
           lines-ROOT-xml nil
           lines-setenv-sh (config/setenv-sh)}}]
  (let [config (tomcat-config :custom-tomcat-home custom-tomcat-home)]
    (write-tomcat-file
      (:config-server-xml config)
      :content lines-server-xml) 
    (write-tomcat-file
      (:config-catalina-properties config)
      :content lines-catalina-properties)
    (write-tomcat-file
      (:webapps-root-xml config)
      :content lines-ROOT-xml)
    (if (:os-package config) ;TODO: this is a problem: consider a liferay on native tomcat and a d2rq in a tomcat-bundle on the same machine --> os-package is true, and every tomcat7 instance acts like it. however, it should be true for the liferay tomcat only
       (write-tomcat-file
         (:config-default config)
         :content lines-etc-default-tomcat7)      
      (write-tomcat-file
        (:config-setenv-sh config)
        :content lines-setenv-sh
        :executable? true))
  ))