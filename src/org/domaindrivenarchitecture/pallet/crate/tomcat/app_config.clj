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
     [org.domaindrivenarchitecture.pallet.crate.tomcat.schema :as schema]
    ))

(def default-server-xml-config
  "The default configuration needed for the server-xml file"
  {:shutdown-port "8005"
   :start-ssl false
   :executor-daemon "true"
   :executor-min-spare-threads "4"
   :executor-max-threads "151"
   :service-name "Catalina"
   :connector-port "8080"
   :connector-protocol "HTTP/1.1"
   :connection-timeout "61000"
   :uri-encoding "UTF-8"
   })

(def default-heap-config
  "The default configuration of the heap settings"
  {:xms "1536m"
   :xmx "2560m"
   :max-perm-size "512m"
   :jdk6 false})

(def default-custom-config
  {:with-manager-webapps false})

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
  [config :- schema/ServerXmlConfig]
  (into 
    []
    (concat      
      ["<?xml version='1.0' encoding='utf-8'?>"
      (str "<Server port=\"" (get-in config [:shutdown-port]) "\" shutdown=\"SHUTDOWN\">")]
      (when (get-in config [:start-ssl])
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
      (str "  <Service name=\"" (get-in config [:service-name]) "\">")
      ""
      "    <Executor name=\"tomcatThreadPool\" namePrefix=\"catalina-exec-\""
      (str "       "
           " daemon=\"" (get-in config [:executor-daemon]) "\""
           " maxThreads=\"" (get-in config [:executor-max-threads]) "\""
           " minSpareThreads=\"" (get-in config [:executor-min-spare-threads]) "\"/>")
      ""
      (str "    <Connector executor=\"tomcatThreadPool\" "
           "port=\"" (get-in config [:connector-port]) "\" "
           "protocol=\"" (get-in config [:connector-protocol]) "\"")
      (str "               "
           "connectionTimeout=\"" (get-in config [:connection-timeout]) "\" "
           "URIEncoding=\"" (-> config :uri-encoding) "\" />")
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
      "</Server>"]))
  )
  
(s/defn setenv-sh
  [config :- schema/JavaVmConfig]
  [(if (get-in config [:jdk6]) 
     "JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-amd64"
     "#JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-amd64")
   (str "JAVA_OPTS=\"$JAVA_OPTS"
        " -server"
        " -Dfile.encoding=UTF8"
        " -Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false"
        " -Duser.timezone=GMT"
        " -Xms" (get-in config [:xms])
        " -Xmx" (get-in config [:xmx])
        " -XX:MaxPermSize=" (get-in config [:max-perm-size]) "\"")
   ]
  )

(s/defn default-tomcat7
  [config :- schema/JavaVmConfig]
  ["TOMCAT7_USER=tomcat7"
   "TOMCAT7_GROUP=tomcat7"
   (if (get-in config [:jdk6]) 
     "JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-amd64"
     "#JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-amd64")
   (str "JAVA_OPTS=\"-Dfile.encoding=UTF8 -Djava.net.preferIPv4Stack=true"
        " -Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false"
        " -Duser.timezone=GMT"
        " -Xms" (get-in config [:xms])
        " -Xmx" (get-in config [:xmx])
        " -XX:MaxPermSize=" (get-in config [:max-perm-size])
        " -XX:+UseConcMarkSweepGC\"")
   "#JAVA_OPTS=\"${JAVA_OPTS} -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n\""
   "TOMCAT7_SECURITY=no"
   "#AUTHBIND=no"]
)