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

(ns dda.pallet.dda-tomcat-crate.infra
  (:require
    [schema.core :as s]
    [pallet.api :as api]
    [dda.pallet.core.dda-crate :as dda-crate]
    [dda.config.commons.map-utils :as map-utils]
    [dda.pallet.dda-tomcat-crate.infra.schema :as schema]
    [dda.pallet.dda-tomcat-crate.infra.app :as app]
    [dda.pallet.dda-tomcat-crate.infra.app-config :as app-config]))


(def TomcatConfig
  schema/TomcatConfig)

(def default-config
  "Tomcat Crate Default Configuration"
  {:server-xml-config app-config/default-server-xml-config
   :java-vm-config app-config/default-heap-config
   :custom-config app-config/default-custom-config
   :default-lines (app-config/default-tomcat7 app-config/default-heap-config)
   :setenv-sh-lines (app-config/setenv-sh app-config/default-heap-config)})


(s/defn tomcat-defaults
  "Provides a map with all tomcat configurations. If parameter
custom-home is provided, then a custom tomcat is installed. In
other case the default ubuntu package is used."
  [java-vm-config :- schema/JavaVmConfig
   custom-config :- schema/CustomConfig]
  (let [os-package (not (contains? custom-config :custom-tomcat-home))
        tomcat-home (if os-package
                      "/var/lib/tomcat7/"
                      (get-in custom-config [:custom-tomcat-home]))
        config-base (if os-package
                      "/etc/tomcat7/"
                      (str (get-in custom-config [:custom-tomcat-home]) "conf/"))
        custom-tomcat-bin (if os-package
                            "/usr/share/tomcat7/bin/"
                            (str tomcat-home "bin/"))]
   {:os-package os-package
    :tomcat-home-location tomcat-home
    :config-base-location config-base
    :webapps-location (str tomcat-home "webapps/")
    :custom-bin-location custom-tomcat-bin
    :config-default-location "/etc/default/tomcat7"
    :config-server-xml-location (str config-base "server.xml")
    :config-catalina-properties-location (str config-base "catalina.properties")
    :config-setenv-sh-location (str custom-tomcat-bin "setenv.sh")
    :webapps-root-xml-location (str config-base "Catalina/localhost/ROOT.xml")
    :java-package (if (get-in java-vm-config [:jdk6])
                    "openjdk-6-jdk"
                    "openjdk-7-jdk")
    :download-url "http://apache.openmirror.de/tomcat/tomcat-7/v7.0.68/bin/apache-tomcat-7.0.68.tar.gz"}))


(s/defn ^:always-validate merge-config :- TomcatConfig
  "merges the partial config with default config & ensures that resulting config is valid."
  [partial-config]
  (let [config (map-utils/deep-merge
                 default-config
                 partial-config)]
    (map-utils/deep-merge
      (tomcat-defaults
        (get-in config [:java-vm-config])
        (get-in config [:custom-config]))
      config)))


(s/defn install
  "install function for httpd-crate."
  [config :- TomcatConfig]
  (app/install-tomcat7 config))

(s/defn configure
  "configure function for httpd-crate."
  [config :- TomcatConfig]
  (app/configure-tomcat7 config))

(defmethod dda-crate/dda-install
  :dda-tomcat [dda-crate partial-effective-config]
  (let [config (dda-crate/merge-config dda-crate partial-effective-config)]
    (install config)))

(defmethod dda-crate/dda-configure
  :dda-tomcat [dda-crate partial-effective-config]
  (let [config (dda-crate/merge-config dda-crate partial-effective-config)]
    (configure config)))

(def dda-tomcat-crate
  (dda-crate/make-dda-crate
    :facility :dda-tomcat
    :version [0 1 0]
    :config-schema TomcatConfig
    :config-default default-config))


(def with-tomcat
  (dda-crate/create-server-spec dda-tomcat-crate))
