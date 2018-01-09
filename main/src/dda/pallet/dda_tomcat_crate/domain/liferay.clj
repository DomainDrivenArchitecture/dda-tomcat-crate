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

(ns dda.pallet.dda-tomcat-crate.domain.liferay
  (:require
   [schema.core :as s]
   [dda.pallet.dda-tomcat-crate.infra :as infra]
   [dda.config.commons.directory-model :as dir-model]
   [dda.pallet.dda-tomcat-crate.infra.catalina-properties :as catalina-properties]
   [dda.pallet.dda-tomcat-crate.infra.root-xml :as root-xml]))

; -------------------  schemas  ---------------------
(def LR6
  {:lr-6x {:xmx-megabbyte s/Num                   ; e.g. 6072 or 2560
           :lr-home dir-model/NonRootDirectory}}) ; e.g. /var/lib/liferay

(def LR7
  {:lr-7x {}})

(def DomainConfig
  "Represents the tomcat for liferay configuration."
  (s/either
    LR6
    LR7))

; ----------------  fields and functions  -------------
(def os-user "tomcat7")
(def tomcat-home-dir "/etc/tomcat7/")

;this does not neet to be changed for LR7
(def etc-tomcat7-Catalina-localhost-ROOT-xml
  ["<Context path=\"\" crossContext=\"true\">"
   ""
   "  <!-- JAAS -->"
   ""
   "  <!--<Realm"
   "      className=\"org.apache.catalina.realm.JAASRealm\""
   "      appName=\"PortalRealm\""
   "      userClassNames=\"com.liferay.portal.kernel.security.jaas.PortalPrincipal\""
   "      roleClassNames=\"com.liferay.portal.kernel.security.jaas.PortalRole\""
   "  />-->"
   " "
   "  <!--"
   "  Uncomment the following to disable persistent sessions across reboots."
   "  -->"
   " "
   "  <!--<Manager pathname=\"\" />-->"
   " "
   "  <!--"
   "  Uncomment the following to not use sessions. See the property"
   "  \"session.disabled\" in portal.properties."
   "  -->"
   " "
   "  <!--<Manager className=\"com.liferay.support.tomcat.session.SessionLessManagerBase\" />-->"
   ""
   "</Context>"])

(def etc-tomcat7-catalina-properties-lines
  [
   "# Licensed to the Apache Software Foundation (ASF) under one or more"
   "# contributor license agreements.  See the NOTICE file distributed with"
   "# this work for additional information regarding copyright ownership."
   "# The ASF licenses this file to You under the Apache License, Version 2.0"
   ""
   "# (the \"License\"); you may not use this file except in compliance with"
   "# the License.  You may obtain a copy of the License at"
   "#"
   "#     http://www.apache.org/licenses/LICENSE-2.0"
   "#"
   "# Unless required by applicable law or agreed to in writing, software"
   "# distributed under the License is distributed on an \"AS IS\" BASIS,"
   "# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied."
   "# See the License for the specific language governing permissions and"
   "# limitations under the License."
   ""
   "#"
   "# List of comma-separated packages that start with or equal this string"
   "# will cause a security exception to be thrown when"
   "# passed to checkPackageAccess unless the"
   "# corresponding RuntimePermission (\"accessClassInPackage.\"+package) has"
   "# been granted."
   ""
   "package.access=sun.,org.apache.catalina.,org.apache.coyote.,org.apache.tomcat.,org.apache.jasper."
   "#"
   "# List of comma-separated packages that start with or equal this string"
   "# will cause a security exception to be thrown when"
   "# passed to checkPackageDefinition unless the"
   "# corresponding RuntimePermission (\"defineClassInPackage.\"+package) has"
   "# been granted."
   "#"
   "# by default, no packages are restricted for definition, and none of"
   "# the class loaders supplied with the JDK call checkPackageDefinition."
   "#"
   "package.definition=sun.,java.,org.apache.catalina.,org.apache.coyote.,org.apache.tomcat.,org.apache.jasper."
   ""
   "#"
   "#"
   "# List of comma-separated paths defining the contents of the \"common\""
   "# classloader. Prefixes should be used to define what is the repository type."
   "# Path may be relative to the CATALINA_HOME or CATALINA_BASE path or absolute."
   "# If left as blank,the JVM system loader will be used as Catalina's \"common\""
   "# loader."
   "# Examples:"
   "#     \"foo\": Add this folder as a class repository"
   "#     \"foo/*.jar\": Add all the JARs of the specified folder as class"
   "#                  repositories"
   "#     \"foo/bar.jar\": Add bar.jar as a class repository"
   "common.loader=${catalina.base}/lib,${catalina.base}/lib/*.jar,${catalina.home}/lib,${catalina.home}/lib/*.jar,/var/lib/liferay/lib/*.jar"
   ""
   "#"
   "# List of comma-separated paths defining the contents of the \"server\""
   "# classloader. Prefixes should be used to define what is the repository type."
   "# Path may be relative to the CATALINA_HOME or CATALINA_BASE path or absolute."
   "# If left as blank, the \"common\" loader will be used as Catalina's \"server\""
   "# loader."
   "# Examples:"
   "#     \"foo\": Add this folder as a class repository"
   "#     \"foo/*.jar\": Add all the JARs of the specified folder as class"
   "#                  repositories"
   "#     \"foo/bar.jar\": Add bar.jar as a class repository"
   "server.loader=/var/lib/tomcat7/server/classes,/var/lib/tomcat7/server/*.jar"
   ""
   "#"
   "# List of comma-separated paths defining the contents of the \"shared\""
   "# classloader. Prefixes should be used to define what is the repository type."
   "# Path may be relative to the CATALINA_BASE path or absolute. If left as blank,"
   "# the \"common\" loader will be used as Catalina's \"shared\" loader."
   "# Examples:"
   "#     \"foo\": Add this folder as a class repository"
   "#     \"foo/*.jar\": Add all the JARs of the specified folder as class"
   "#                  repositories"
   "#     \"foo/bar.jar\": Add bar.jar as a class repository"
   "# Please note that for single jars, e.g. bar.jar, you need the URL form"
   "# starting with file:."
   "shared.loader=/var/lib/tomcat7/shared/classes,/var/lib/tomcat7/shared/*.jar"
   ""
   "# List of JAR files that should not be scanned for configuration information"
   "# such as web fragments, TLD files etc. It must be a comma separated list of"
   "# JAR file names."
   "# The JARs listed below include:"
   "# - Tomcat Bootstrap JARs"
   "# - Tomcat API JARs"
   "# - Catalina JARs"
   "# - Jasper JARs"
   "# - Tomcat JARs"
   "# - Common non-Tomcat JARs"
   "# - Sun JDK JARs"
   "# - Apple JDK JARs"
   "tomcat.util.scan.DefaultJarScanner.jarsToSkip=\\"
   "bootstrap.jar,commons-daemon.jar,tomcat-juli.jar,\\"
   "annotations-api.jar,el-api.jar,jsp-api.jar,servlet-api.jar,\\"
   "catalina.jar,catalina-ant.jar,catalina-ha.jar,catalina-tribes.jar,\\"
   "jasper.jar,jasper-el.jar,ecj-*.jar,\\"
   "tomcat-api.jar,tomcat-util.jar,tomcat-coyote.jar,tomcat-dbcp.jar,\\"
   "tomcat-i18n-en.jar,tomcat-i18n-es.jar,tomcat-i18n-fr.jar,tomcat-i18n-ja.jar,\\"
   "tomcat-juli-adapters.jar,catalina-jmx-remote.jar,catalina-ws.jar,\\"
   "tomcat-jdbc.jar,\\"
   "commons-beanutils*.jar,commons-codec*.jar,commons-collections*.jar,\\"
   "commons-dbcp*.jar,commons-digester*.jar,commons-fileupload*.jar,\\"
   "commons-httpclient*.jar,commons-io*.jar,commons-lang*.jar,commons-logging*.jar,\\"
   "commons-math*.jar,commons-pool*.jar,\\"
   "jstl.jar,\\"
   "geronimo-spec-jaxrpc*.jar,wsdl4j*.jar,\\"
   "ant.jar,ant-junit*.jar,aspectj*.jar,jmx.jar,h2*.jar,hibernate*.jar,httpclient*.jar,\\"
   "jmx-tools.jar,jta*.jar,log4j*.jar,mail*.jar,slf4j*.jar,\\"
   "xercesImpl.jar,xmlParserAPIs.jar,xml-apis.jar,\\"
   "dnsns.jar,ldapsec.jar,localedata.jar,sunjce_provider.jar,sunmscapi.jar,\\"
   "sunpkcs11.jar,jhall.jar,tools.jar,\\"
   "sunec.jar,zipfs.jar,\\"
   "apple_provider.jar,AppleScriptEngine.jar,CoreAudio.jar,dns_sd.jar,\\"
   "j3daudio.jar,j3dcore.jar,j3dutils.jar,jai_core.jar,jai_codec.jar,\\"
   "mlibwrapper_jai.jar,MRJToolkit.jar,vecmath.jar,\\"
   "junit.jar,junit-*.jar,ant-launcher.jar"
   ""
   "#"
   "# String cache configuration."
   "tomcat.util.buf.StringCache.byte.enabled=true"
   "#tomcat.util.buf.StringCache.char.enabled=true"
   "#tomcat.util.buf.StringCache.trainThreshold=500000"
   "#tomcat.util.buf.StringCache.cacheSize=5000"])

(s/defn
  lr-6x-infra-configuration :- infra/InfraResult
  [domain-config :- LR6]
  (let [{:keys [xmx-megabbyte lr-home]} domain-config]
   {infra/facility
    {:server-xml
      {:shutdown-port "8005"
       :start-ssl true
       :executor-daemon "false"
       :executor-min-spare-threads "48"
       :executor-max-threads "151"
       :service-name "Catalina"
       :connector-port "8009"
       :connector-protocol "AJP/1.3"
       :connection-timeout "61000"
       :uri-encoding "UTF-8"
       :config-server-xml-location "/etc/tomcat7/server.xml"
       :os-user os-user}
     :tomct-vm
      {:managed {:config-default-location "/etc/default/tomcat7"}
       :settings #{:prefer-ipv4 :disable-cl-clear-ref
                   :conc-mark-sweep-gc :timezone-gmt
                   :disable-tomcat-security}
       :xmx (str xmx-megabbyte "m")
       :xms "1536m"
       :max-perm-size "512m"
       :os-user os-user
       :java-home "/usr/lib/jvm/java-1.8.0-openjdk-amd64"
       :catalina-opts (str "-Dcustom.lr.dir=" lr-home)}
     ;there is no longer support for java6 & java7, you've to install manually:
     ;https://askubuntu.com/questions/67909/how-do-i-install-oracle-jdk-6
     :java
      {:java-version 8}
     :tomcat-source
      {:tomcat-managed {:package-name "tomcat7"}}
     :catalina-properties {:os-user os-user
                           :config-catalina-properties-location tomcat-home-dir
                           :lines etc-tomcat7-catalina-properties-lines}
     :root-xml {:os-user os-user
                :webapps-root-xml-location tomcat-home-dir
                :lines etc-tomcat7-Catalina-localhost-ROOT-xml}}}))

(s/defn
  infra-configuration :- infra/InfraResult
  [domain-config :- DomainConfig]
  (let [{:keys [lr-6x lr7x]} domain-config]
    (when (contains? domain-config :lr-6x)
      (lr-6x-infra-configuration lr-6x))))
