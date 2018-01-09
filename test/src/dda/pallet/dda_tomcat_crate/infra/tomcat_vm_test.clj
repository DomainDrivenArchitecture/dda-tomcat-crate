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

(ns dda.pallet.dda-tomcat-crate.infra.tomcat-vm-test
  (:require
    [clojure.test :refer :all]
    [schema.core :as s]
    [dda.pallet.dda-tomcat-crate.infra.tomcat-vm :as sut]))

(defn has? [expected actual]
  (not (empty? (filter #(= % expected) actual))))

(defn hasnt? [expected actual]
  (empty? (filter #(= % expected) actual)))

(def setenv-sh-config
  {:os-user "tomcat7"
   :java-home "/usr/lib/jvm/java-1.7.0-openjdk-amd64"
   :xms "1m"
   :xmx "2m"
   :max-perm-size "3m"
   :settings #{}
   :download {:config-setenv-sh-location ""}})

(def liferay-setenv-sh-config
  {:os-user "tomcat7"
   :java-home "/usr/lib/jvm/java-1.6.0-openjdk-amd64"
   :xms "1536m"
   :xmx "2560m"
   :max-perm-size "512m"
   :settings #{:prefer-ipv4 :disable-cl-clear-ref :conc-mark-sweep-gc
               :disable-tomcat-security :timezone-gmt}
   :download {:config-setenv-sh-location ""}
   :catalina-opts "-Dcustom.lr.dir=/var/lib/liferay"})

(deftest test-validity
  (testing
    (is
      (s/validate sut/TomcatVmConfig setenv-sh-config))
    (is
      (s/validate sut/TomcatVmConfig liferay-setenv-sh-config))))

(deftest test-java-opts
  (testing
    (is (= "-Xms1m -Xmx2m -XX:MaxPermSize=3m"
          (sut/java-opts setenv-sh-config)))
    (is (= "-Djava.net.preferIPv4Stack=true -Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false -Duser.timezone=GMT -Xms1536m -Xmx2560m -XX:MaxPermSize=512m -XX:+UseConcMarkSweepGC"
          (sut/java-opts liferay-setenv-sh-config)))))

(deftest test-setenv-sh-config
  (testing
    (is (has?
          "JAVA_OPTS=\"-Djava.awt.headless=true -server -Dfile.encoding=UTF8 -Xms1m -Xmx2m -XX:MaxPermSize=3m\""
          (sut/tomcat-env setenv-sh-config)))
    (is (has?
          "#TOMCAT7_SECURITY=no"
          (sut/tomcat-env setenv-sh-config)))
    (is (hasnt?
          "CATALINA_OPTS=\"\""
          (sut/tomcat-env setenv-sh-config)))))

(deftest test-liferay-setenv-sh-config
  (testing
    (is (has?
          "TOMCAT7_SECURITY=no"
          (sut/tomcat-env liferay-setenv-sh-config)))
    (is (hasnt?
          "CATALINA_OPTS=\"-Dcustom.lr.dir=/var/lib/liferay\""
          (sut/tomcat-env liferay-setenv-sh-config)))))

(def expected-liferay-setenv-sh-lines
  ["TOMCAT7_USER=tomcat7"
   "TOMCAT7_GROUP=tomcat7"
   "JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-amd64"
   (str "JAVA_OPTS=\"${JAVA_OPTS} -server -Dfile.encoding=UTF8"
        " -Djava.net.preferIPv4Stack=true"
        " -Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false"
        " -Duser.timezone=GMT -Xms1536m -Xmx2560m -XX:MaxPermSize=512m"
        " -XX:+UseConcMarkSweepGC\"")
   "#JAVA_OPTS=\"${JAVA_OPTS} -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n\""
   "CATALINA_OPTS=\"-Dcustom.lr.dir=/var/lib/liferay\""
   "TOMCAT7_SECURITY=no"])
