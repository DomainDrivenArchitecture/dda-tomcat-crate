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
(ns dda.pallet.dda-tomcat-crate.infra.tomcat-vm
   (:require
     [clojure.string :as string]
     [selmer.parser :as selmer]
     [schema.core :as s]
     [pallet.actions :as actions]))

(def BaseTomcatVmConfig
  {:tomcat-version (s/enum 7 8)
   :os-user s/Str
   :java-home s/Str
   :xms s/Str
   :xmx s/Str
   :max-perm-size s/Str
   :settings (hash-set (s/enum :prefer-ipv4 :disable-cl-clear-ref
                               :conc-mark-sweep-gc :timezone-gmt
                               :disable-tomcat-security))
   (s/optional-key :catalina-opts) s/Str})

(def TomcatVmConfig
  (s/either
    (merge
      BaseTomcatVmConfig
      {:download {:config-setenv-sh-location s/Str}})
    (merge
      BaseTomcatVmConfig
      {:managed {:config-default-location s/Str}})))

(s/defn
  java-opts :- s/Str
  "builds the java opts string."
  [config :- TomcatVmConfig]
  (let [{:keys [xms xmx max-perm-size settings]} config]
    (str
      (when (contains? settings :prefer-ipv4)
        "-Djava.net.preferIPv4Stack=true ")
      (when (contains? settings :disable-cl-clear-ref)
        "-Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false ")
      (when (contains? settings :timezone-gmt)
        "-Duser.timezone=GMT ")
      "-Xms" xms
      " -Xmx" xmx
      " -XX:MaxPermSize=" max-perm-size
      (when (contains? settings :conc-mark-sweep-gc)
       " -XX:+UseConcMarkSweepGC"))))

(s/defn
  tomcat-env :- [s/Str]
  "the tomcat default vm generator function."
  [config :- TomcatVmConfig]
  (let [{:keys [settings]} config
        template-file (cond
                          (= 7 (:tomcat-version config)) "etc_default_tomcat7.template"
                          :else "etc_default_tomcat8.template")]
    (string/split
      (selmer/render-file template-file
                          (merge
                            config
                            {:java-opts (java-opts config)
                             :contains-catalina-opts? (contains? config :catalina-opts)
                             :wo-tomcat-security? (contains? settings :disable-tomcat-security)}))
      #"\n")))

(s/defn configure-tomcat-vm
  [config :- TomcatVmConfig]
  (let [{:keys [os-user download managed]} config]
    (when (contains? config :managed)
      (actions/remote-file
        (:config-default-location managed)
        :owner "root"
        :group os-user
        :mode "644"
        :literal true
        :content (string/join
                   \newline
                   (tomcat-env config))))
    (when (contains? config :download)
        (actions/remote-file
          (:config-setenv-sh-location download)
          :owner os-user
          :group os-user
          :mode "755"
          :literal true
          :content (string/join
                     \newline
                     (tomcat-env config))))))
