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

(def setenv-sh-config
  {:os-user "tomcat7"
   :java-home "/usr/lib/jvm/java-1.7.0-openjdk-amd64"
   :xms "1m"
   :xmx "2m"
   :max-perm-size "3m"
   :custom {:config-setenv-sh-location ""}})

(def expected-setenv-sh-lines
  ["JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-amd64"
   (str "JAVA_OPTS=\"$JAVA_OPTS -server -Dfile.encoding=UTF8"
        " -Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false"
        " -Duser.timezone=GMT -Xms1m -Xmx2m -XX:MaxPermSize=3m\"")])

(deftest test-setenv-sh
  (testing
    (is
      (s/validate sut/TomcatVmConfig setenv-sh-config))
    (is
      (= expected-setenv-sh-lines
         (sut/setenv-sh setenv-sh-config)))))
