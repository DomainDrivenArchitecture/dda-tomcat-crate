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
  {:shutdown-port "8005"
   :start-ssl false
   :executor-daemon "true"
   :executor-min-spare-threads "4"
   :executor-max-threads "152"
   :service-name "Catalina"
   :connector-port "8080"
   :connector-protocol "HTTP/1.1"
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

(deftest test-server-xml
  (testing
    (is
      (s/validate sut/ServerXmlConfig server-xml-config))
    (is
      (= expected-server-xml-lines
         (sut/server-xml server-xml-config)))))
