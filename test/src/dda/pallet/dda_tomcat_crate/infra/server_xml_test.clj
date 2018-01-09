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
; limitations under the License
(ns dda.pallet.dda-tomcat-crate.infra.server-xml-test
  (:require
    [clojure.test :refer :all]
    [schema.core :as s]
    [dda.pallet.dda-tomcat-crate.infra.server-xml :as sut]))

(defn has? [expected actual]
  (not (empty? (filter #(= % expected) actual))))

(defn hasnt? [expected actual]
  (empty? (filter #(= % expected) actual)))

(def server-xml-config
  {:config-server-xml-location ""
   :os-user ""
   :shutdown-port "8005"
   :start-ssl false
   :executor-daemon "true"
   :executor-min-spare-threads "4"
   :executor-max-threads "152"
   :service-name "Catalina"
   :connector-port "8080"
   :connector-protocol "HTTP/1.1"
   :connection-timeout "12345"
   :uri-encoding "UTF-8"})

(def server-xml-config-wo-url-encoding
  {:config-server-xml-location ""
   :os-user ""
   :shutdown-port "8005"
   :start-ssl false
   :executor-daemon "true"
   :executor-min-spare-threads "4"
   :executor-max-threads "152"
   :service-name "Catalina"
   :connector-port "8080"
   :connector-protocol "HTTP/1.1"
   :connection-timeout "20000"})

(def server-xml-config-ajp
  {:config-server-xml-location ""
   :os-user ""
   :shutdown-port "8005"
   :start-ssl true
   :executor-daemon "false"
   :executor-max-threads "152"
   :executor-min-spare-threads "4"
   :service-name "Catalina"
   :connector-port "8009"
   :connector-protocol "AJP/1.3"
   :connection-timeout "20000"
   :uri-encoding "UTF-8"})

(deftest test-validity
  (testing
    (is
      (s/validate sut/ServerXmlConfig server-xml-config))
    (is
      (s/validate sut/ServerXmlConfig server-xml-config-wo-url-encoding))
    (is
      (s/validate sut/ServerXmlConfig server-xml-config-ajp))))

(deftest test-server-xml-config
  (testing
    (is (has? "<Server port=\"8005\" shutdown=\"SHUTDOWN\">"
              (sut/server-xml server-xml-config)))
    (is (hasnt? "  <Listener className=\"org.apache.catalina.core.AprLifecycleListener\" SSLEngine=\"on\" />"
                (sut/server-xml server-xml-config)))
    (is (has? "               URIEncoding=\"UTF-8\""
              (sut/server-xml server-xml-config)))
    (is (has? "               connectionTimeout=\"12345\" />"
              (sut/server-xml server-xml-config)))))

(deftest test-server-xml-config-wo-url-encoding
  (testing
    (is (hasnt?  "               URIEncoding=\"UTF-8\""
                 (sut/server-xml server-xml-config-wo-url-encoding)))))

(deftest test-server-server-xml-config-ajp
  (testing
    (is (has? "  <Listener className=\"org.apache.catalina.core.AprLifecycleListener\" SSLEngine=\"on\" />"
              (sut/server-xml server-xml-config-ajp)))
    (is (has? "               port=\"8009\""
              (sut/server-xml server-xml-config-ajp)))
    (is (has? "               protocol=\"AJP/1.3\""
              (sut/server-xml server-xml-config-ajp)))))
