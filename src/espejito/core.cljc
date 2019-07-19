;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns espejito.core
  #?(:cljs (:require-macros espejito.core))
  (:require
   #?(:cljs [goog.string :as gstring])
   #?(:cljs [goog.string.format])
   #?(:cljs [cljs.pprint    :include-macros true :as pp]
      :clj  [clojure.pprint :as pp])
   [espejito.internal :as i]))



(def ^:dynamic *metrics* nil)


(defmacro measure
  "Use this at layer boundaries in code. Not recommended for tight loops - may cause out-of-memory situation!"
  [name & body]
  (let [esym (gensym "ex")]
    `(if (some? *metrics*)
       (let [start# (i/nanos)
             children-metrics# (transient [])]
         (try
           (let [result# (binding [*metrics* children-metrics#]
                           ~@body)]
             (conj! *metrics* [~name (i/nanos start#) nil
                               (persistent! children-metrics#)])
             result#)
           (catch ~(if (:ns &env) `js/Error `Throwable) ~esym
             (conj! *metrics* [~name (i/nanos start#) ~(if (:ns &env)
                                                         `(if-let [ctor# (.-constructor ~esym)]
                                                            (.-name ctor#)
                                                            "unknown")
                                                         `(.getName ^Class (class ~esym)))
                               (persistent! children-metrics#)])
             (throw ~esym))))
       (do
         ~@body))))


(defn wrap-measure
  "Wrap given function such that it measures latency when invoked."
  [f measure-name]
  (fn espejito-measured [& args]
    (measure measure-name
             (apply f args))))


(defn print-table
  "Print the report in a tabular format. Argument table-printer is an arity-2 fn that accepts header and rows."
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
                    :cumul-% (#?(:cljs gstring/format :clj format) "%.2f %%" (i/percent cumulative-latency-ns total-ns))
                    :individual (i/human-readable-latency individual-latency-ns)
                    :indiv-% (#?(:cljs gstring/format :clj format) "%.2f %%" (i/percent individual-latency-ns total-ns)))))
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