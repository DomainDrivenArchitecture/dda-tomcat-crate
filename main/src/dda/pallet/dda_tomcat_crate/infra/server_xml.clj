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
(ns dda.pallet.dda-tomcat-crate.infra.server-xml
   (:require
     [clojure.string :as string]
     [schema.core :as s]))

(def ServerXmlConfig
  {:shutdown-port s/Str
   :start-ssl s/Bool
   :executor-daemon s/Str
   :executor-max-threads s/Str
   :executor-min-spare-threads s/Str
   :service-name s/Str
   :connector-port s/Str
   :connector-protocol (s/pred #(contains? #{"HTTP/1.1" "AJP/1.3"} %))
   :connection-timeout s/Str
   (s/optional-key :uri-encoding) s/Str})

(s/defn server-xml
  "the server-xml generator function."
  [config :- ServerXmlConfig]
  (into
    []
    (concat
      ["<?xml version='1.0' encoding='utf-8'?>"
       (str "<Server port=\"" (:shutdown-port config) "\" shutdown=\"SHUTDOWN\">")]
      (when (contains? config :start-ssl)
          ["  <Listener className=\"org.apache.catalina.core.AprLifecycleListener\" SSLEngine=\"on\" />"])
      ["  <Listener className=\"org.apache.catalina.core.JasperListener\" />"
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
       (str "  <Service name=\"" (:service-name config) "\">")
       ""
       "    <Executor name=\"tomcatThreadPool\" namePrefix=\"catalina-exec-\""
       (str "       "
            " daemon=\"" (:executor-daemon config) "\""
            " maxThreads=\"" (:executor-max-threads config) "\""
            " minSpareThreads=\"" (:executor-min-spare-threads config) "\"/>")
       ""
       (str "    <Connector executor=\"tomcatThreadPool\" "
            "port=\"" (:connector-port config) "\" "
            "protocol=\"" (:connector-protocol config) "\"")]
      (if (contains? config :uri-encoding)
       [(str "               "
             "connectionTimeout=\"" (:connection-timeout config) "\" "
             "URIEncoding=\"" (:uri-encoding config) "\" />")]
       [(str "               "
             "connectionTimeout=\"" (:connection-timeout config) "\" />")])
      [""
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
       "</Server>"])))
