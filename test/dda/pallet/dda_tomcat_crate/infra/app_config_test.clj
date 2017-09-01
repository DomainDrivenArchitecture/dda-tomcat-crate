(ns dda.pallet.dda-tomcat-crate.infra.app-config-test
  (:require
    [clojure.test :refer :all]
    [schema.core :as s]
    [dda.pallet.dda-tomcat-crate.infra.schema :as schema]
    [dda.pallet.dda-tomcat-crate.infra.app-config :as sut]
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
      (s/validate schema/ServerXmlConfig server-xml-config))
    (is
      (= expected-server-xml-lines
         (sut/server-xml server-xml-config)))
    ))