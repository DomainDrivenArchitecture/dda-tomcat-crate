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

(deftest domain-validation-test
  (testing
    "test the domain spec"
      (is
        (s/validate sut/DomainConfig
                    {:lr-6x {:xmx-megabbyte 2560
                             :lr-home "/var/lib/liferay/"}}))
      (is
        (s/validate sut/DomainConfig
                    {:lr-7x {}}))))
