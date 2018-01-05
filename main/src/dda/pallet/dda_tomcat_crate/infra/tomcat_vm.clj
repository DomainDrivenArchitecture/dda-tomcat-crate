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
     [schema.core :as s]
     [pallet.actions :as actions]))

(def BaseTomcatVmConfig
  {:os-user s/Str
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
      {:custom {:config-setenv-sh-location s/Str}})
    (merge
      BaseTomcatVmConfig
      {:os-package {:config-default-location s/Str}})))

; todo: version-spec
(s/defn
  tomcat-env :- [s/Str]
  [config :- TomcatVmConfig]
  (let [{:keys [os-user java-home xms xmx max-perm-size settings
                catalina-opts]} config]
    (into
      []
      (concat
        [(str "TOMCAT7_USER=" os-user)
         (str "TOMCAT7_GROUP=" os-user)
         (str "JAVA_HOME=" java-home)
         (str "JAVA_OPTS=\"${JAVA_OPTS}"
              " -server"
              " -Dfile.encoding=UTF8"
              (when (contains? settings :prefer-ipv4)
                " -Djava.net.preferIPv4Stack=true")
              (when (contains? settings :disable-cl-clear-ref)
                " -Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false")
              (when (contains? settings :timezone-gmt)
                " -Duser.timezone=GMT")
              " -Xms" xms
              " -Xmx" xmx
              " -XX:MaxPermSize=" max-perm-size
              (when (contains? settings :conc-mark-sweep-gc)
               " -XX:+UseConcMarkSweepGC")
              "\"")
         "#JAVA_OPTS=\"${JAVA_OPTS} -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n\""]
        (when (contains? config :catalina-opts)
          [(str "CATALINA_OPTS=\"" catalina-opts "\"")])
        (when (contains? settings :disable-tomcat-security)
          ["TOMCAT7_SECURITY=no"])))))

(s/defn configure-tomcat-vm
  [config :- TomcatVmConfig]
  (let [{:keys [os-user custom os-package]} config]
    (when (contains? config :os-package)
      (actions/remote-file
        (:config-default-location os-package)
        :owner os-user
        :group os-user
        :mode "644"
        :literal true
        :content (string/join
                   \newline
                   (tomcat-env config))))
    (when (contains? config :custom)
        (actions/remote-file
          (:config-setenv-sh-location custom)
          :owner (:os-user config)
          :group (:os-user config)
          :mode "755"
          :literal true
          :content (string/join
                     \newline
                     (tomcat-env config))))))
