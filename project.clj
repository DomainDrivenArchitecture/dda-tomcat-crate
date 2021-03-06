(defproject dda/dda-tomcat-crate "2.0.2-SNAPSHOT"
  :description "tomcat crate of dda-pallet project"
  :url "https://www.domaindrivenarchitecture.org"
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[dda/dda-pallet "2.2.1"]
                 [selmer "1.11.8"]]
  :repositories [["snapshots" :clojars]
                 ["releases" :clojars]]
  :deploy-repositories [["snapshots" :clojars]
                        ["releases" :clojars]]
  :source-paths ["main/src"]
  :resource-paths ["main/resources"]
  :profiles {:dev
             {:source-paths ["integration/src"
                             "test/src"
                             "uberjar/src"]
              :resource-paths ["integration/resources"
                               "test/resources"]
              :dependencies
              [[org.domaindrivenarchitecture/pallet-aws "0.2.8.2"
                 :exclusions [com.palletops/pallet]]
               [dda/pallet "0.9.0" :classifier "tests"]
               [dda/dda-serverspec-crate "1.1.1"]
               [ch.qos.logback/logback-classic "1.3.0-alpha4"]
               [org.slf4j/jcl-over-slf4j "1.8.0-beta2"]]
              :plugins
              [[lein-sub "0.3.0"]]}
             :repl-options {:init-ns dda.pallet.dda-tomcat-crate.app.instantiate-existing}
             :leiningen/reply
             {:dependencies [[org.slf4j/jcl-over-slf4j "1.8.0-beta2"]]
              :exclusions [commons-logging]}
             :test {:test-paths ["test/src"]
                    :resource-paths ["test/resources"]
                    :dependencies [[dda/pallet "0.9.0" :classifier "tests"]]}
             :uberjar {:source-paths ["uberjar/src"]
                       :resource-paths ["uberjar/resources"]
                       :aot :all
                       :main dda.pallet.dda-tomcat-crate.main
                       :dependencies [[org.clojure/tools.cli "0.3.7"]]}}
   :local-repo-classpath true)
