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


(ns org.domaindrivenarchitecture.pallet.crate.tomcat.app-config
   (:require
     [pallet.actions :as actions]
     [clojure.string :as string]
     [schema.core :as s]
     [org.domaindrivenarchitecture.config.commons.map-utils :as map-utils]
    ))

(def ServerXmlConfig
  "The configuration needed for the server-xml file"
  {:shutdown-port s/Str
   (s/optional-key :ajp-port) s/Any
   (s/optional-key :http-port) s/Str
   :service-name s/Str
   :protocol s/Str
   :executorMaxThreads s/Str
   :connectorMaxThreads s/Str
   :connectionTimeout s/Str
   })

(def defaultServerXmlConfig
  "The default configuration needed for the server-xml file"
  {:shutdown-port "8005"
   :service-name "Catalina"
   :protocol "AJP/1.3"
   :executorMaxThreads "161"
   :connectorMaxThreads "151"
   :connectionTimeout "61000"
   })

;TODO: This variable name should be reviewed.
(def HeapConfig
  "The configuration of the heap settings"
  {:Xms s/Str
   :Xmx s/Str
   :MaxPermSize s/Str
   :jdk6 s/Bool}
  )

(def defaultHeapConfig
  "The default configuration of the heap settings"
  {:Xms "1536m"
   :Xmx "2560m"
   :MaxPermSize "512m"
   :jdk6 false})

(def var-lib-tomcat7-webapps-ROOT-META-INF-context-xml
  ["<Context path=\"/\"" 
   "antiResourceLocking=\"false\" />"])

(def var-lib-tomcat7-webapps-ROOT-index-html
  ["  <?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
   "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\""
   "   \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
   "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">"
   "<head>"
   "    <title>Apache Tomcat</title>"
   "</head>"
   ""
   "<body>"
   "<h1>It works !</h1>"
   ""
   "<p>If you're seeing this page via a web browser, it means you've setup Tomcat successfully. Congratulations!</p>"
   " "
   "<p>This is the default Tomcat home page. It can be found on the local filesystem at: <code>/var/lib/tomcat7/webapps/ROOT/index.html</code></p>"
   ""
   "<p>Tomcat7 veterans might be pleased to learn that this system instance of Tomcat is installed with <code>CATALINA_HOME</code> in <code>/usr/share/tomcat7</code> and <code>CATALINA_BASE</code> in <code>/var/lib/tomcat7</code>, following the rules from <code>/usr/share/doc/tomcat7-common/RUNNING.txt.gz</code>.</p>"
   ""
   "<p>You might consider installing the following packages, if you haven't already done so:</p>"
   ""
   "<p><b>tomcat7-docs</b>: This package installs a web application that allows to browse the Tomcat 7 documentation locally. Once installed, you can access it by clicking <a href=\"docs/\">here</a>.</p>"
   ""
   "<p><b>tomcat7-examples</b>: This package installs a web application that allows to access the Tomcat 7 Servlet and JSP examples. Once installed, you can access it by clicking <a href=\"examples/\">here</a>.</p>"
   ""
   "<p><b>tomcat7-admin</b>: This package installs two web applications that can help managing this Tomcat instance. Once installed, you can access the <a href=\"manager/html\">manager webapp</a> and the <a href=\"host-manager/html\">host-manager webapp</a>.<p>"
   ""
   "<p>NOTE: For security reasons, using the manager webapp is restricted to users with role \"manager-gui\". The host-manager webapp is restricted to users with role \"admin-gui\". Users are defined in <code>/etc/tomcat7/tomcat-users.xml</code>.</p>"
   ""
   "</body>"
   "</html>"
   ]  
  )

(s/defn server-xml
  "the server-xml generator function."
  [config :- ServerXmlConfig]
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
  (str "<Server port=\"" (get-in config :shutdown-port) "\" shutdown=\"SHUTDOWN\">")
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
  (str "  <Service name=\"" (get-in config :service-name) "\">")
  ""
  "    <!--The connectors can use a shared executor, you can define one or more named thread pools-->"
  "    <!--"
  "    <Executor name=\"tomcatThreadPool\" namePrefix=\"catalina-exec-\""
  (str "        maxThreads=\"" (get-in config :executorMaxThreads) "\" minSpareThreads=\"4\"/>")
  "    -->"
  ""
  ""
  "    <!-- A \"Connector\" represents an endpoint by which requests are received"
  "         and responses are returned. Documentation at :"
  "         Java HTTP Connector: /docs/config/http.html (blocking & non-blocking)"
  "         Java AJP  Connector: /docs/config/ajp.html"
  "         APR (HTTP/AJP) Connector: /docs/apr.html"
  "         Define a non-SSL HTTP/1.1 Connector on port 8080"
  "    -->"
  (str "    <Connector port=\"" (get-in config :http-port) "\" protocol=\"" (get-in config :protocol) "\"")
  (str "               connectionTimeout=\"" (get-in config :connectionTimeout) "\"")
  "               URIEncoding=\"UTF-8\""
  "               redirectPort=\"8443\" />"
  "    <!-- A \"Connector\" using the shared thread pool-->"
  "    <!--"
  (str "    <Connector executor=\"" (get-in config :executor) "\"")
  (str "               port=\"" (get-in config :http-port) "\" protocol=\"" (get-in config :protocol) "\"")
  (str "               connectionTimeout=\"" (get-in config :connectionTimeout) "\"")
  "               redirectPort=\"8443\" />"
  "    -->"
  "    <!-- Define a SSL HTTP/1.1 Connector on port 8443"
  "         This connector uses the JSSE configuration, when using APR, the"
  "         connector should be using the OpenSSL style configuration"
  "         described in the APR documentation -->"
  "    <!--"
  "    <Connector port=\"8443\" protocol=\"HTTP/1.1\" SSLEnabled=\"true\""
  "               maxThreads=\"150\" scheme=\"https\" secure=\"true\""
  "               clientAuth=\"false\" sslProtocol=\"TLS\" />"
  "    -->"
  ""
  "    <!-- Define an AJP 1.3 Connector on port 8009 -->"
  "    <!--"
  "    <Connector port=\"8009\" protocol=\"AJP/1.3\" redirectPort=\"8443\" />"
  "    -->"
  ""
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
  
(s/defn setenv-sh
  [config :- HeapConfig]
  [(if (get-in config [:jdk6]) 
     "JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-amd64"
     "#JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-amd64")
   (str "JAVA_OPTS=\"$JAVA_OPTS"
        " -Dfile.encoding=UTF8"
        " -Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false"
        " -Duser.timezone=GMT"
        " -Xms" (get-in config [:Xms])
        " -Xmx" (get-in config [:Xmx])
        " -XX:MaxPermSize=" (get-in config [:MaxPermSize]) "\"")
   ]
  )

(s/defn default-tomcat7
  [config :- HeapConfig]
  ["TOMCAT7_USER=tomcat7"
   "TOMCAT7_GROUP=tomcat7"
   (if (get-in config [:jdk6]) 
     "JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-amd64"
     "#JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-amd64")
   (str "JAVA_OPTS=\"-Dfile.encoding=UTF8 -Djava.net.preferIPv4Stack=true"
        " -Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false"
        " -Duser.timezone=GMT"
        " -Xms" (get-in config [:Xms])
        " -Xmx" (get-in config [:Xmx])
        " -XX:MaxPermSize=" (get-in config [:MaxPermSize])
        " -XX:+UseConcMarkSweepGC\"")
   "#JAVA_OPTS=\"${JAVA_OPTS} -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n\""
   "TOMCAT7_SECURITY=no"
   "#AUTHBIND=no"]
)