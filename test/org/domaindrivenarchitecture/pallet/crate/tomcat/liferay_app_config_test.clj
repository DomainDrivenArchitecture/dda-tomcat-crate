(ns org.domaindrivenarchitecture.pallet.crate.tomcat.liferay-app-config-test
  (:require
    [clojure.test :refer :all]
    [schema.core :as s]
    [org.domaindrivenarchitecture.pallet.crate.tomcat.schema :as schema]
    [org.domaindrivenarchitecture.pallet.crate.tomcat.app-config :as sut]
   ))

(def server-xml-config-case-ajp
  {:shutdown-port "8005"
   :start-ssl true
   :executor-daemon "false"
   :executor-max-threads "152"
   :executor-min-spare-threads "4"
   :service-name "Catalina"
   :connector-port "8009"
   :connector-protocol "AJP/1.3"
   :connection-timeout "20000"})

(def expected-server-xml-lines-case-ajp
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
    "</Server>"]
  )

(deftest test-server-xml-case-ajp
  (testing
    (is 
      (s/validate schema/ServerXmlConfig server-xml-config-case-ajp))
    (is
      (= expected-server-xml-lines-case-ajp
         (sut/server-xml server-xml-config-case-ajp)))
    ))