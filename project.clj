(defproject dda/dda-tomcat-crate "0.2.2-SNAPSHOT"
  :description "tomcat crate of dda-pallet project"
  :url "https://www.domaindrivenarchitecture.org"
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [prismatic/schema "1.1.7"]
                 [dda/dda-pallet "0.5.5-SNAPSHOT"]
                 [dda/dda-pallet-commons "0.7.0"]]
  :repositories [["snapshots" :clojars]
                 ["releases" :clojars]]
  :deploy-repositories [["snapshots" :clojars]
                        ["releases" :clojars]]
  :source-paths ["main/src"]
  :profiles {:dev
             {:source-paths ["integration/src"
                             "test/src"
                             "uberjar/src"]
              :resource-paths ["integration/resources"
                               "test/resources"]
              :dependencies
              [[org.domaindrivenarchitecture/pallet-aws "0.2.8.2"]
               [com.palletops/pallet "0.8.12" :classifier "tests"]
               [dda/dda-serverspec-crate "0.2.2-SNAPSHOT"]
               [ch.qos.logback/logback-classic "1.2.3"]
               [org.slf4j/jcl-over-slf4j "1.8.0-alpha2"]]
              :plugins
              [[lein-sub "0.3.0"]]}
             :leiningen/reply
             {:dependencies [[org.slf4j/jcl-over-slf4j "1.8.0-alpha2"]]
              :exclusions [commons-logging]}
             :test {:test-paths ["test/src"]
                    :resource-paths ["test/resources"]
                    :dependencies [[com.palletops/pallet "0.8.12" :classifier "tests"]]}
             :uberjar {:source-paths ["uberjar/src"]
                       :resource-paths ["uberjar/resources"]
                       :aot :all
                       :main dda.pallet.dda-tomcat-crate.main
                       :dependencies [[org.clojure/tools.cli "0.3.5"]]}}
   :local-repo-classpath true)
