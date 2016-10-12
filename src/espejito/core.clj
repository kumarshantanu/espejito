;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns espejito.core
  (:require
    [clojure.pprint :as pp]
    [espejito.internal :as i]))


(def ^:dynamic *metrics* nil)


(defmacro measure
  "Use this at layer boundaries in code. Not recommended for tight loops - may cause out-of-memory situation!"
  [name & body]
  `(if *metrics*
     (let [start# (System/nanoTime)
           children-metrics# (transient [])]
       (try
         (let [result# (binding [*metrics* children-metrics#]
                         ~@body)]
           (conj! *metrics* [~name (- (System/nanoTime) start#) nil
                             (persistent! children-metrics#)])
           result#)
         (catch Throwable e#
           (conj! *metrics* [~name (- (System/nanoTime) start#) (.getName ^Class (class e#))
                             (persistent! children-metrics#)])
           (throw e#))))
     (do
       ~@body)))


(defn print-table
  "Print the report in a tabular format."
  ([nested-metrics]
    (print-table 50 nested-metrics))
  ([^long name-column-width nested-metrics]
    (print-table pp/print-table name-column-width nested-metrics))
  ([table-printer ^long name-column-width nested-metrics]
    (if-let [flat-metrics (seq (i/flatten-children-report nested-metrics))]
      (let [total-ns (:cumulative-latency-ns (first flat-metrics))]
        (->> flat-metrics
          (mapv (fn [{:keys [name level cumulative-latency-ns individual-latency-ns] :as m}]
                  (assoc m
                    :name (i/indent-name name-column-width level name)
                    :cumulative (i/human-readable-latency cumulative-latency-ns)
                    :cumul-% (format "%.2f %%" (i/percent cumulative-latency-ns total-ns))
                    :individual (i/human-readable-latency individual-latency-ns)
                    :indiv-% (format "%.2f %%" (i/percent individual-latency-ns total-ns)))))
          (table-printer [:name :cumulative :cumul-% :individual :indiv-% :thrown?])))
      (println "\nNo data to report!"))))


(defmacro report
  "Use this at the outermost periphery of the code."
  [f & body]
  `(binding [*metrics* (transient [])]
     (try
       ~@body
       (finally
         (~f (persistent! *metrics*))))))