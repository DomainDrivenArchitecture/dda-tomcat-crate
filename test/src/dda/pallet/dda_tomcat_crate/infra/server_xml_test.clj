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

(def server-xml-config
  {:config-server-xml-location ""
   :shutdown-port "8005"
   :start-ssl false
   :executor-daemon "true"
   :executor-min-spare-threads "4"
   :executor-max-threads "152"
   :service-name "Catalina"
   :connector-port "8080"
   :connector-protocol "HTTP/1.1"
   :connection-timeout "20000"
   :uri-encoding "UTF-8"})

(def server-xml-config-wo-url-encoding
  {:config-server-xml-location ""
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

(def expected-server-xml-lines
  ["<?xml version='1.0' encoding='utf-8'?>"
   "<Server port=\"8005\" shutdown=\"SHUTDOWN\">"
   "  <Listener className=\"org.apache.catalina.core.JasperListener\" />"
   "  <Listener className=\"org.apache.catalina.core.JreMemoryLeakPreventionListener\" />"
   "  <Listener className=\"org.apache.catalina.mbeans.GlobalResourcesLifecycleListener\" />"
   "  <Listener className=\"org.apache.catalina.core.ThreadLocalLeakPreventionListener\" />"
   ""
   "  <GlobalNamingResources>"
   "    <Resource name=\"UserDatabase\" auth=\"Container\""
   "              type=\"org.apache.catalina.UserDatabase\""
   "              description=\"User database that can be updated and saved\""
   "              factory=\"org.apache.catalina.users.MemoryUserDatabaseFactory\""
   "              pathname=\"conf/tomcat-users.xml\" />"
   "  </GlobalNamingResources>"
   ""
   "  <Service name=\"Catalina\">"
   ""
   "    <Executor name=\"tomcatThreadPool\" namePrefix=\"catalina-exec-\""
   "        daemon=\"true\" maxThreads=\"152\" minSpareThreads=\"4\"/>"
   ""
   "    <Connector executor=\"tomcatThreadPool\" port=\"8080\" protocol=\"HTTP/1.1\""
   "               connectionTimeout=\"20000\" URIEncoding=\"UTF-8\" />"
   ""
   "    <Engine name=\"Catalina\" defaultHost=\"localhost\">"
   ""
   "      <Realm className=\"org.apache.catalina.realm.LockOutRealm\"/>"
   "      <Realm className=\"org.apache.catalina.realm.UserDatabaseRealm\""
   "             resourceName=\"UserDatabase\"/>"
   ""
   "      <Host name=\"localhost\"  appBase=\"webapps\""
   "            unpackWARs=\"true\" autoDeploy=\"true\">"
   ""
   "      <Valve className=\"org.apache.catalina.valves.AccessLogValve\" directory=\"logs\""
   "            pattern=\"%h %l %u %t &quot;%r&quot; %s %b %D %S\""
   "            prefix=\"localhost_access_log.\" suffix=\".txt\""
   "            resolveHosts=\"false\"/>"
   "      </Host>"
   "    </Engine>"
   "  </Service>"
   "</Server>"])

(def expected-server-xml-lines-ajp
  ["<?xml version='1.0' encoding='utf-8'?>"
    "<Server port=\"8005\" shutdown=\"SHUTDOWN\">"
    "  <Listener className=\"org.apache.catalina.core.AprLifecycleListener\" SSLEngine=\"on\" />"
    "  <Listener className=\"org.apache.catalina.core.JasperListener\" />"
    "  <Listener className=\"org.apache.catalina.core.JreMemoryLeakPreventionListener\" />"
    "  <Listener className=\"org.apache.catalina.mbeans.GlobalResourcesLifecycleListener\" />"
    "  <Listener className=\"org.apache.catalina.core.ThreadLocalLeakPreventionListener\" />"
    ""
    "  <GlobalNamingResources>"
    "    <Resource name=\"UserDatabase\" auth=\"Container\""
    "              type=\"org.apache.catalina.UserDatabase\""
    "              description=\"User database that can be updated and saved\""
    "              factory=\"org.apache.catalina.users.MemoryUserDatabaseFactory\""
    "              pathname=\"conf/tomcat-users.xml\" />"
    "  </GlobalNamingResources>"
    ""
    "  <Service name=\"Catalina\">"
    ""
    "    <Executor name=\"tomcatThreadPool\" namePrefix=\"catalina-exec-\""
    "        daemon=\"false\" maxThreads=\"152\" minSpareThreads=\"4\"/>"
    ""
    "    <Connector executor=\"tomcatThreadPool\" port=\"8009\" protocol=\"AJP/1.3\""
    "               connectionTimeout=\"20000\" URIEncoding=\"UTF-8\" />"
    ""
    "    <Engine name=\"Catalina\" defaultHost=\"localhost\">"
    ""
    "      <Realm className=\"org.apache.catalina.realm.LockOutRealm\"/>"
    "      <Realm className=\"org.apache.catalina.realm.UserDatabaseRealm\""
    "             resourceName=\"UserDatabase\"/>"
    ""
    "      <Host name=\"localhost\"  appBase=\"webapps\""
    "            unpackWARs=\"true\" autoDeploy=\"true\">"
    ""
    "      <Valve className=\"org.apache.catalina.valves.AccessLogValve\" directory=\"logs\""
    "            pattern=\"%h %l %u %t &quot;%r&quot; %s %b %D %S\""
    "            prefix=\"localhost_access_log.\" suffix=\".txt\""
    "            resolveHosts=\"false\"/>"
    "      </Host>"
    "    </Engine>"
    "  </Service>"
    "</Server>"])

(deftest test-server-xml
  (testing
    (is
      (s/validate sut/ServerXmlConfig server-xml-config))
    (is
      (s/validate sut/ServerXmlConfig server-xml-config-wo-url-encoding))
    (is
      (s/validate sut/ServerXmlConfig server-xml-config-ajp))
    (is
      (= expected-server-xml-lines
         (sut/server-xml server-xml-config)))
    (is (filter
          #(= % "               connectionTimeout=\"20000\" />")
          (sut/server-xml server-xml-config-wo-url-encoding)))
    (is
      (= expected-server-xml-lines-ajp
         (sut/server-xml server-xml-config-ajp)))))
