(ns espejito.core-test
  (:require [clojure.test :refer :all]
            [espejito.core :as e]
            [clojure.pprint :as pp]))

(deftest the-test
  (testing "The only test"
    (e/report pp/print-table
      (e/measure "outer"
        (Thread/sleep 1000)
        (try
          (e/measure "inner"
            (Thread/sleep 2000)
            (throw (UnsupportedOperationException. "foo")))
          (catch Exception _))))))
