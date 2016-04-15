(defproject org.domaindrivenarchitecture/dda-tomcat-crate "0.1.1"
  :description "The dda tomcat crate"
  :url "https://www.domaindrivenarchitecture.org"
  :pallet {:source-paths ["src"]}
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [prismatic/schema "1.0.5"]
                 [metosin/schema-tools "0.7.0"]
                 [com.palletops/pallet "0.8.11"]
                 [com.palletops/stevedore "0.8.0-beta.7"]
                 [org.domaindrivenarchitecture/dda-config-commons "0.1.0"]
                 [org.domaindrivenarchitecture/dda-config-crate "0.3.1"]]
  :profiles {:dev
             {:dependencies
              [[com.palletops/pallet "0.8.11" :classifier "tests"]
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