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

(ns dda.pallet.dda-tomcat-crate.domain-test
  (:require
    [schema.core :as s]
    [clojure.test :refer :all]
    [dda.pallet.dda-tomcat-crate.domain :as sut]))

(def pair1 {:input {:lr-6x {:xmx-megabyte 2560
                            :lr-home "/var/lib/liferay/"}}
            :expected {:dda-tomcat
                         {:server-xml
                          {:tomcat-version 7,
                           :executor-max-threads "151",
                           :service-name "Catalina",
                           :connector-port "8009",
                           :executor-daemon "false",
                           :uri-encoding "UTF-8",
                           :config-server-xml-location "/etc/tomcat7/server.xml",
                           :connection-timeout "61000",
                           :start-ssl true,
                           :os-user "tomcat7",
                           :connector-protocol "AJP/1.3",
                           :shutdown-port "8005",
                           :executor-min-spare-threads "48"},
                          :tomct-vm
                          {:tomcat-version 7,
                           :catalina-opts "-Dcustom.lr.dir=/var/lib/liferay/",
                           :settings
                           #{:timezone-gmt :disable-tomcat-security
                             :conc-mark-sweep-gc :prefer-ipv4 :disable-cl-clear-ref},
                           :xmx "2560m",
                           :managed
                           {:config-default-location "/etc/default/tomcat7"},
                           :xms "1536m",
                           :max-perm-size "512m",
                           :os-user "tomcat7",
                           :java-home "/usr/lib/jvm/java-1.8.0-openjdk-amd64"},
                          :java {:java-version 8},
                          :tomcat-source {:tomcat-managed {:package-name "tomcat7"}},
                          :catalina-properties
                          {:tomcat-version 7,
                           :os-user "tomcat7",
                           :config-catalina-properties-location
                           "/etc/tomcat7/catalina.properties",
                           :common-loader ",/var/lib/liferay/lib/*.jar"},
                          :root-xml
                          {:os-user "tomcat7",
                           :webapps-root-xml-location
                           "/etc/tomcat7/Catalina/localhost/ROOT.xml",
                           :lines
                           ["<Context path=\"\" crossContext=\"true\">"
                            ""
                            "    <!-- JAAS -->"
                            ""
                            "    <!--<Realm"
                            "        className=\"org.apache.catalina.realm.JAASRealm\""
                            "        appName=\"PortalRealm\""
                            "        userClassNames=\"com.liferay.portal.kernel.security.jaas.PortalPrincipal\""
                            "        roleClassNames=\"com.liferay.portal.kernel.security.jaas.PortalRole\""
                            "    />-->"
                            ""
                            "    <!--"
                            "    Uncomment the following to disable persistent sessions across reboots."
                            "    -->"
                            ""
                            "    <!--<Manager pathname=\"\" />-->"
                            ""
                            "    <!--"
                            "    Uncomment the following to not use sessions. See the property"
                            "    \"session.disabled\" in portal.properties."
                            "    -->"
                            ""
                            "    <!--<Manager className=\"com.liferay.support.tomcat.session.SessionLessManagerBase\" />-->"
                            "</Context>"]}}}})

(def pair2 {:input {:lr-7x {:xmx-megabyte 2560
                            :lr-home "/var/lib/liferay/"}}
            :expected {:dda-tomcat
                         {:server-xml
                          {:tomcat-version 8,
                           :executor-max-threads "151",
                           :service-name "Catalina",
                           :connector-port "8009",
                           :executor-daemon "false",
                           :uri-encoding "UTF-8",
                           :config-server-xml-location "/etc/tomcat8/server.xml",
                           :connection-timeout "61000",
                           :start-ssl true,
                           :os-user "tomcat8",
                           :connector-protocol "AJP/1.3",
                           :shutdown-port "8005",
                           :executor-min-spare-threads "48"},
                          :tomct-vm
                          {:tomcat-version 8,
                           :catalina-opts "-Dcustom.lr.dir=/var/lib/liferay/",
                           :settings
                           #{:timezone-gmt :disable-tomcat-security
                             :conc-mark-sweep-gc :prefer-ipv4 :disable-cl-clear-ref},
                           :xmx "2560m",
                           :managed
                           {:config-default-location "/etc/default/tomcat8"},
                           :xms "1536m",
                           :max-perm-size "512m",
                           :os-user "tomcat8",
                           :java-home "/usr/lib/jvm/java-1.8.0-openjdk-amd64"},
                          :java {:java-version 8},
                          :tomcat-source {:tomcat-managed {:package-name "tomcat8"}},
                          :catalina-properties
                          {:tomcat-version 8,
                           :os-user "tomcat8",
                           :config-catalina-properties-location
                           "/etc/tomcat8/catalina.properties",
                           :common-loader ",\"/var/lib/liferay/lib/*.jar\""},
                          :catalina-policy
                          {:os-user "tomcat8",
                           :catalina-policy-location "/etc/tomcat8/catalina.policy"},
                          :root-xml
                          {:os-user "tomcat8",
                           :webapps-root-xml-location
                           "/etc/tomcat8/Catalina/localhost/ROOT.xml",
                           :lines
                           ["<Context path=\"\" crossContext=\"true\">"
                            ""
                            "    <!-- JAAS -->"
                            ""
                            "    <!--<Realm"
                            "        className=\"org.apache.catalina.realm.JAASRealm\""
                            "        appName=\"PortalRealm\""
                            "        userClassNames=\"com.liferay.portal.kernel.security.jaas.PortalPrincipal\""
                            "        roleClassNames=\"com.liferay.portal.kernel.security.jaas.PortalRole\""
                            "    />-->"
                            ""
                            "    <!--"
                            "    Uncomment the following to disable persistent sessions across reboots."
                            "    -->"
                            ""
                            "    <!--<Manager pathname=\"\" />-->"
                            ""
                            "    <!--"
                            "    Uncomment the following to not use sessions. See the property"
                            "    \"session.disabled\" in portal.properties."
                            "    -->"
                            ""
                            "    <!--<Manager className=\"com.liferay.support.tomcat.session.SessionLessManagerBase\" />-->"
                            "</Context>"]}}}})

(def pair3 {:input {:standalone {:xmx-megabyte 2560}}
            :expected {:dda-tomcat
                       {:server-xml
                        {:tomcat-version 8,
                         :executor-max-threads "151",
                         :service-name "Catalina",
                         :connector-port "8080",
                         :executor-daemon "true",
                         :uri-encoding "UTF-8",
                         :config-server-xml-location "/etc/tomcat8/server.xml",
                         :connection-timeout "61000",
                         :start-ssl false,
                         :os-user "tomcat8",
                         :connector-protocol "HTTP/1.1",
                         :shutdown-port "8005",
                         :executor-min-spare-threads "4"},
                        :tomct-vm
                        {:tomcat-version 8,
                         :managed
                         {:config-default-location "/etc/default/tomcat8"},
                         :settings #{},
                         :xmx "512m",
                         :xms "512m",
                         :max-perm-size "128m",
                         :os-user "tomcat8",
                         :java-home "/usr/lib/jvm/java-1.8.0-openjdk-amd64"},
                        :java {:java-version 8},
                        :tomcat-source {:tomcat-managed {:package-name "tomcat8"}}}}})


(deftest domain-validation-test
  (testing
    "test the domain spec"
      (is (= (:expected pair1)
             (sut/infra-configuration (:input pair1))))
      (is (= (:expected pair2)
             (sut/infra-configuration (:input pair2))))
      (is (= (:expected pair3)
             (sut/infra-configuration (:input pair3))))))
