(ns espejito.core-test
  (:require
    [clojure.pprint :as pp]
    [clojure.test :refer :all]
    [espejito.core     :as e]
    [espejito.internal :as i]))


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
  (testing "Using clojure.pprint/pprint (to show nested data)"
    (e/report pp/pprint
      (processing)))
  (testing "Using vanilla print-table (to show intermediate flattened data)"
    (e/report #(-> (i/flatten-children-report %)
                 pp/print-table)
      (processing)))
  (testing "Using espejito print-table"
    (e/report e/print-table
      (processing)))
  (testing "Empty block using espejito print-table"
    (e/report e/print-table)))
