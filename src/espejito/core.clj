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


(def print-table (partial pp/print-table [:name :cumulative-latency :individual-latency :error?]))


(defmacro report
  "Use this at the outermost periphery of the code."
  [f & body]
  `(binding [*metrics* (transient [])]
     (try
       ~@body
       (finally
         (~f (let [result# (transient [])]
               (i/collect-children-report result# 0 (persistent! *metrics*))
               (persistent! result#)))))))