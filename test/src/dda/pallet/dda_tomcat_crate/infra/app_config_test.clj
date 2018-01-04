(ns dda.pallet.dda-tomcat-crate.infra.app-config-test
  (:require
    [clojure.test :refer :all]
    [schema.core :as s]
    [dda.pallet.dda-tomcat-crate.infra.schema :as schema]
    [dda.pallet.dda-tomcat-crate.infra.app-config :as sut]))


(def setenv-sh-config
  {:xms "1m"
   :xmx "2m"
   :max-perm-size "3m"
   :jdk 7})


(def expected-setenv-sh-lines
  ["JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-amd64"
   (str "JAVA_OPTS=\"$JAVA_OPTS -server -Dfile.encoding=UTF8"
        " -Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false"
        " -Duser.timezone=GMT -Xms1m -Xmx2m -XX:MaxPermSize=3m\"")])

(deftest test-setenv-sh
  (testing
    (is
      (s/validate schema/TomcatVmConfig setenv-sh-config))
    (is
      (= expected-setenv-sh-lines
         (sut/setenv-sh setenv-sh-config)))))
