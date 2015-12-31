(defproject org.domaindrivenarchitecture/dda-tomcat-crate "0.1.0"
  :description "The dda tomcat crate"
  :url "https://www.domaindrivenarchitecture.org"
  :pallet {:source-paths ["src"]}
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.palletops/leaven "0.3.1"]
                 [com.palletops/pallet "0.8.2"]
                 [com.palletops/pallet "0.8.2" :classifier "tests"]
                 [com.palletops/stevedore "0.8.0-beta.7"]
                 [ch.qos.logback/logback-classic "1.0.9"]
                 [org.domaindrivenarchitecture.org/dda-config-crate "0.2.0"]]
  :profiles {:dev
             {:dependencies
              [[com.palletops/pallet "0.8.2" :classifier "tests"]
               ]
              :plugins
              [[com.palletops/pallet-lein "0.8.0-alpha.1"]]}
              :leiningen/reply
               {:dependencies [[org.slf4j/jcl-over-slf4j "1.7.2"]]
                :exclusions [commons-logging]}}
   :local-repo-classpath true
   :repositories [["snapshots" :clojars]
                  ["releases" :clojars]]
   :deploy-repositories [["snapshots" :clojars]
                         ["releases" :clojars]]
   :classifiers {:tests {:source-paths ^:replace ["test"]
                         :resource-paths ^:replace []}})