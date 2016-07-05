(ns org.domaindrivenarchitecture.pallet.crate.tomcat.app-config-test
  (:require
    [clojure.test :refer :all]
    [schema.core :as s]
    [org.domaindrivenarchitecture.pallet.crate.tomcat.schema :as schema]
    [org.domaindrivenarchitecture.pallet.crate.tomcat.app-config :as sut]
   ))

(def setenv-sh-config
  {:xms "1m"
   :xmx "2m"
   :max-perm-size "3m"
   :jdk6 false})


(def expected-setenv-sh-lines
  ["#JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-amd64"
   (str "JAVA_OPTS=\"$JAVA_OPTS -server -Dfile.encoding=UTF8"
        " -Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false"
        " -Duser.timezone=GMT -Xms1m -Xmx2m -XX:MaxPermSize=3m\"")])

(deftest test-setenv-sh
  (testing
    (is 
      (s/validate schema/JavaVmConfig setenv-sh-config))
    (is
      (= expected-setenv-sh-lines
         (sut/setenv-sh setenv-sh-config)))
    ))


(def server-xml-config
  {:shutdown-port "8005"
   :executor-max-threads "152"
   :service-name "Catalina"
   :connector-port "8080"
   :connector-protocol "HTTP/1.1"
   :connection-timeout "20000"})
  
(def expected-server-xml-lines
  ["<?xml version='1.0' encoding='utf-8'?>"
  "<!--"
  " Licensed to the Apache Software Foundation (ASF) under one or more"
  "  contributor license agreements.  See the NOTICE file distributed with"
  "  this work for additional information regarding copyright ownership."
  "  The ASF licenses this file to You under the Apache License, Version 2.0"
  "  (the \"License\"); you may not use this file except in compliance with"
  "  the License.  You may obtain a copy of the License at"
  ""
  "      http://www.apache.org/licenses/LICENSE-2.0"
  ""
  "  Unless required by applicable law or agreed to in writing, software"
  "  distributed under the License is distributed on an \"AS IS\" BASIS,"
  "  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied."
  "  See the License for the specific language governing permissions and"
  "  limitations under the License."
  "-->"
  "<!-- Note:  A \"Server\" is not itself a \"Container\", so you may not"
  "     define subcomponents such as \"Valves\" at this level."
  "     Documentation at /docs/config/server.html"
  "-->"
  "<Server port=\"8005\" shutdown=\"SHUTDOWN\">"
  "  <!-- Security listener. Documentation at /docs/config/listeners.html"
  "  <Listener className=\"org.apache.catalina.security.SecurityListener\" />"
  "  -->"
  "  <!--APR library loader. Documentation at /docs/apr.html -->"
  "  <!--"
  "  <Listener className=\"org.apache.catalina.core.AprLifecycleListener\" SSLEngine=\"on\" />"
  "  -->"
  "  <!--Initialize Jasper prior to webapps are loaded. Documentation at /docs/jasper-howto.html -->"
  "  <Listener className=\"org.apache.catalina.core.JasperListener\" />"
  "  <!-- Prevent memory leaks due to use of particular java/javax APIs-->"
  "  <Listener className=\"org.apache.catalina.core.JreMemoryLeakPreventionListener\" />"
  "  <Listener className=\"org.apache.catalina.mbeans.GlobalResourcesLifecycleListener\" />"
  "  <Listener className=\"org.apache.catalina.core.ThreadLocalLeakPreventionListener\" />"
  ""
  "  <!-- Global JNDI resources"
  "       Documentation at /docs/jndi-resources-howto.html"
  "  -->"
  "  <GlobalNamingResources>"
  "    <!-- Editable user database that can also be used by"
  "         UserDatabaseRealm to authenticate users"
  "    -->"
  "    <Resource name=\"UserDatabase\" auth=\"Container\""
  "              type=\"org.apache.catalina.UserDatabase\""
  "              description=\"User database that can be updated and saved\""
  "              factory=\"org.apache.catalina.users.MemoryUserDatabaseFactory\""
  "              pathname=\"conf/tomcat-users.xml\" />"
  "  </GlobalNamingResources>"
  ""
  "  <!-- A \"Service\" is a collection of one or more \"Connectors\" that share"
  "       a single \"Container\" Note:  A \"Service\" is not itself a \"Container\","
  "       so you may not define subcomponents such as \"Valves\" at this level."
  "       Documentation at /docs/config/service.html"
  "   -->"
  "  <Service name=\"Catalina\">"
  ""
  "    <!--The connectors can use a shared executor, you can define one or more named thread pools-->"
  "    <Executor name=\"tomcatThreadPool\" namePrefix=\"catalina-exec-\""
  "        maxThreads=\"152\" minSpareThreads=\"4\"/>"
  ""
  "    <Connector executor=\"tomcatThreadPool\" port=\"8080\" protocol=\"HTTP/1.1\""
  "               connectionTimeout=\"20000\" URIEncoding=\"UTF-8\" />"
  ""
  "    <!-- An Engine represents the entry point (within Catalina) that processes"
  "         every request.  The Engine implementation for Tomcat stand alone"
  "         analyzes the HTTP headers included with the request, and passes them"
  "         on to the appropriate Host (virtual host)."
  "         Documentation at /docs/config/engine.html -->"
  ""
  "    <!-- You should set jvmRoute to support load-balancing via AJP ie :"
  "    <Engine name=\"Catalina\" defaultHost=\"localhost\" jvmRoute=\"jvm1\">"
  "    -->"
  "    <Engine name=\"Catalina\" defaultHost=\"localhost\">"
  ""
  "      <!--For clustering, please take a look at documentation at:"
  "          /docs/cluster-howto.html  (simple how to)"
  "          /docs/config/cluster.html (reference documentation) -->"
  "      <!--"
  "      <Cluster className=\"org.apache.catalina.ha.tcp.SimpleTcpCluster\"/>"
  "      -->"
  ""
  "      <!-- Use the LockOutRealm to prevent attempts to guess user passwords"
  "           via a brute-force attack -->"
  "      <Realm className=\"org.apache.catalina.realm.LockOutRealm\">"
  "        <!-- This Realm uses the UserDatabase configured in the global JNDI"
  "             resources under the key \"UserDatabase\".  Any edits"
  "             that are performed against this UserDatabase are immediately"
  "             available for use by the Realm.  -->"
  "        <Realm className=\"org.apache.catalina.realm.UserDatabaseRealm\""
  "               resourceName=\"UserDatabase\"/>"
  "      </Realm>"
  ""
  "      <Host name=\"localhost\"  appBase=\"webapps\""
  "            unpackWARs=\"true\" autoDeploy=\"true\">"
  ""
  "        <!-- SingleSignOn valve, share authentication between web applications"
  "             Documentation at: /docs/config/valve.html -->"
  "        <!--"
  "        <Valve className=\"org.apache.catalina.authenticator.SingleSignOn\" />"
  "        -->"
  ""
  "        <!-- Access log processes all example."
  "             Documentation at: /docs/config/valve.html"
  "             Note: The pattern used is equivalent to using pattern=\"common\" -->"
  "        <Valve className=\"org.apache.catalina.valves.AccessLogValve\" directory=\"logs\""
  "               prefix=\"localhost_access_log.\" suffix=\".txt\""
  "               pattern=\"%h %l %u %t &quot;%r&quot; %s %b\" />"
  ""
  "      </Host>"
  "    </Engine>"
  "  </Service>"
  "</Server>"])
  
(deftest test-server-xml
  (testing
    (is 
      (s/validate schema/ServerXmlConfig server-xml-config))
    (is
      (= expected-server-xml-lines
         (sut/server-xml server-xml-config)))
    ))