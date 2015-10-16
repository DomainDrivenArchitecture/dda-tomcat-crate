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
    [pallet.actions :as actions]
    [org.domaindrivenarchitecture.pallet.crate.tomcat.app :as sut]
    ))


(def custom-dir "/etc/pp/")

(deftest tomcat-config-test
  (testing
    (is 
      (=
        "/var/somwhereelse/bin"
        (:custom-bin (sut/tomcat-config :custom-tomcat-home  "/var/somwhereelse"))))
    (is 
      (=
        "/var/somwhereelse/webapps"
        (:webapps (sut/tomcat-config :custom-tomcat-home  "/var/somwhereelse"))))
    (is 
      (=
        "/var/lib/tomcat7/webapps"
        (:webapps (sut/tomcat-config))))
    (is 
      (=
        "openjdk-6-jdk"
        (:java-package (sut/tomcat-config :custom-java-version :6))))
    (is 
      (=
        "openjdk-7-jdk"
        (:java-package (sut/tomcat-config))))
    ))