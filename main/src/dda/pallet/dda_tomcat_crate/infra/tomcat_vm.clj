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
   :max-perm-size s/Str})

(def TomcatVmConfig
  (s/either
    (merge
      BaseTomcatVmConfig
      {:custom {:config-setenv-sh-location s/Str}})
    (merge
      BaseTomcatVmConfig
      {:os-package {:config-default-location s/Str}})))

(s/defn setenv-sh
  [config :- TomcatVmConfig]
  (let [{:keys [java-home xms xmx max-perm-size]} config]
    [(str "JAVA_HOME=" java-home)
     (str "JAVA_OPTS=\"$JAVA_OPTS"
          " -server"
          " -Dfile.encoding=UTF8"
          " -Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false"
          " -Duser.timezone=GMT"
          " -Xms" xms
          " -Xmx" xmx
          " -XX:MaxPermSize=" max-perm-size "\"")]))

; todo: version-spec
(s/defn default-tomcat
  [config :- TomcatVmConfig]
  (let [{:keys [os-user java-home xms xmx max-perm-size]} config]
    [(str "TOMCAT7_USER=" os-user)
     (str "TOMCAT7_GROUP=" os-user)
     (str "JAVA_HOME=" java-home)
     (str "JAVA_OPTS=\"-Dfile.encoding=UTF8 -Djava.net.preferIPv4Stack=true"
          " -Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false"
          " -Duser.timezone=GMT"
          " -Xms" xms
          " -Xmx" xmx
          " -XX:MaxPermSize=" max-perm-size
          " -XX:+UseConcMarkSweepGC\"")
     "#JAVA_OPTS=\"${JAVA_OPTS} -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n\""
     "TOMCAT7_SECURITY=no"
     "#AUTHBIND=no"]))

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
                   (default-tomcat config))))
    (when (contains? config :custom)
        (actions/remote-file
          (:config-setenv-sh-location custom)
          :owner (:os-user config)
          :group (:os-user config)
          :mode "755"
          :literal true
          :content (string/join
                     \newline
                     (setenv-sh config))))))
