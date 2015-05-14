(ns espejito.core-test
  (:require [clojure.test :refer :all]
            [espejito.core :as e]
            [clojure.pprint :as pp]))


(defn processing
  []
  (e/measure "outer"
    (Thread/sleep 100)
    (try
      (e/measure "inner"
        (Thread/sleep 150)
        (e/measure "inner-most"
          (Thread/sleep 50))
        (throw (UnsupportedOperationException. "foo")))
      (catch Exception _))
    (e/measure "inner-sibling"
      (Thread/sleep 50))))


(deftest the-test
  (testing "Using vanilla print-table"
    (e/report pp/print-table
      (processing)))
  (testing "Using espejito print-table"
    (e/report e/print-table
      (processing))))
