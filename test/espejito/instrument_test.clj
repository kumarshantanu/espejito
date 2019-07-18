;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns espejito.instrument-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [espejito.core :as e]
   [espejito.instrument :as ins]))


(defn baz [] (Thread/sleep 100) :baz)
(defn bar [] (Thread/sleep 200) (baz) :bar)
(defn foo [] (Thread/sleep 300) (bar) :foo)


(deftest test-instrument
  ;; instrument
  (doseq [each ["espejito.instrument-test/foo"
                "espejito.instrument-test/bar"
                "espejito.instrument-test/baz"]]
    (ins/instrument-measure each))
  ;; execute and report
  (e/report e/print-table
            (is (= :foo (foo)))))
