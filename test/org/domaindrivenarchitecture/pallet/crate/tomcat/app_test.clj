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

(ns org.domaindrivenarchitecture.pallet.crate.tomcat.app-test
  (:require
    [clojure.test :refer :all]
    [schema.core :as s]
    [schema-tools.core :as st]
    [pallet.actions :as actions]
    [org.domaindrivenarchitecture.pallet.crate.tomcat.app :as sut]
    [org.domaindrivenarchitecture.pallet.crate.tomcat.app-config :as config]
    [org.domaindrivenarchitecture.pallet.crate.tomcat :as tomcat]
    ))


(def partial-config 
 {:custom-config {:with-manager-webapps false}})

(def config
  (tomcat/merge-config partial-config))

(def custom-dir "/etc/pp/")

(def expected-config
  {:webapps-root-xml "/etc/tomcat7/Catalina/localhost/ROOT.xml"
   :os-package true
   :download-url
   "http://apache.openmirror.de/tomcat/tomcat-7/v7.0.68/bin/apache-tomcat-7.0.68.tar.gz",
   :config-catalina-properties "/etc/tomcat7/catalina.properties"
   :java-package "openjdk-7-jdk"
   :config-default "/etc/default/tomcat7"
   :config-server-xml "/etc/tomcat7/server.xml"
   :config-base "/etc/tomcat7"
   :tomcat-home "/var/lib/tomcat7"
   :with-manager-webapps false
   :custom-bin "/usr/share/tomcat7/bin"
   :webapps "/var/lib/tomcat7/webapps"
   :config-setenv-sh "/usr/share/tomcat7/bin/setenv.sh"})

(deftest tomcat-config-test
    (testing
      (is 
        (=
          false
          (get-in (sut/tomcat-config (get-in config [:custom-config])
                                     (get-in config [:java-vm-config])) 
                  [:with-manager-webapps])))
      (is
        (=
          (sut/tomcat-config (get-in config [:custom-config])
                             (get-in config [:java-vm-config])) 
          expected-config        
          )
        )
      )
    )
      