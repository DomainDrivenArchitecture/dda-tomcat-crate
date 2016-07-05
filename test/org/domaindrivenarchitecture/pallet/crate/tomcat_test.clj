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

(ns org.domaindrivenarchitecture.pallet.crate.tomcat-test
  (:require
    [schema.core :as s]
    [schema-tools.core :as st]
    [clojure.test :refer :all]
    [org.domaindrivenarchitecture.pallet.crate.tomcat :as sut]
    ))

(def partial-config 
 {:custom-config {:with-manager-webapps false}})

(def expected-config
  {:webapps-root-xml-location "/etc/tomcat7/Catalina/localhost/ROOT.xml"
   :os-package true
   :tomcat-home-location "/var/lib/tomcat7/"
   :config-base-location "/etc/tomcat7/"
   :webapps-location "/var/lib/tomcat7/webapps/"
   :config-catalina-properties-location "/etc/tomcat7/catalina.properties"
   :config-default-location "/etc/default/tomcat7"
   :config-server-xml-location "/etc/tomcat7/server.xml"
   :custom-bin-location "/usr/share/tomcat7/bin/"
   :config-setenv-sh-location "/usr/share/tomcat7/bin/setenv.sh"
   :java-package "openjdk-7-jdk"
   :download-url
   "http://apache.openmirror.de/tomcat/tomcat-7/v7.0.68/bin/apache-tomcat-7.0.68.tar.gz",
   :custom-config {:with-manager-webapps false},
   :server-xml-config {:shutdown-port "8005",
                       :executor-max-threads "151",
                       :service-name "Catalina",
                       :connector-port "8080",
                       :connector-protocol "HTTP/1.1",
                       :connection-timeout "61000"}
   :java-vm-config {:xms "1536m",
                    :xmx "2560m",
                    :max-perm-size "512m",
                    :jdk6 false},
   :default-lines ["TOMCAT7_USER=tomcat7"
                   "TOMCAT7_GROUP=tomcat7"
                   "#JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-amd64"
                   "JAVA_OPTS=\"-Dfile.encoding=UTF8 -Djava.net.preferIPv4Stack=true -Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false -Duser.timezone=GMT -Xms1536m -Xmx2560m -XX:max-perm-size=512m -XX:+UseConcMarkSweepGC\""
                   "#JAVA_OPTS=\"${JAVA_OPTS} -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n\""
                   "TOMCAT7_SECURITY=no"
                   "#AUTHBIND=no"],
   :setenv-sh-lines ["#JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-amd64"
                     "JAVA_OPTS=\"$JAVA_OPTS -server -Dfile.encoding=UTF8 -Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false -Duser.timezone=GMT -Xms1536m -Xmx2560m -XX:MaxPermSize=512m\""],
   })

(deftest config-test
  (testing 
    "test if the default config is valid"
    (is (sut/merge-config partial-config))))

(deftest tomcat-config-test
  (testing
    (is (=
          false
          (get-in (sut/merge-config partial-config)
                  [:custom-config :with-manager-webapps])))
    (is (=
          expected-config
          (sut/merge-config partial-config)))
    ))

(deftest server-spec
  (testing 
    "test the server spec" 
      (is sut/with-tomcat)
      ))