(ns espejito.core
  (:require [espejito.internal :as i]))


(def ^:dynamic *metrics* nil)


(defmacro measure
  "Use this at layer boundaries in code. Not recommended for tight loops - may cause out-of-memory situation!"
  [name & body]
  `(if *metrics*
     (let [start# (System/nanoTime)]
       (try
         (let [result# (do ~@body)]
           (conj! *metrics* [~name (- (System/nanoTime) start#) nil])
           result#)
         (catch Throwable e#
           (conj! *metrics* [~name (- (System/nanoTime) start#) (.getName ^Class (class e#))])
           (throw e#))))
     (do
       ~@body)))


(defmacro report
  "Use this at the outermost periphery of the code."
  [f & body]
  `(binding [*metrics* (transient [])]
     (try
       ~@body
       (finally
         (~f (->> (persistent! *metrics*)
               (reduce (fn [result# [name# cumulative-latency-ns# error?#]]
                         (conj result# {:name name#
                                        :cumulative-latency-ns cumulative-latency-ns#
                                        :cumulative-latency (i/human-latency cumulative-latency-ns#)
                                        :individual-latency (i/human-latency (if-let [inner-layer# (last result#)]
                                                                               (- cumulative-latency-ns#
                                                                                 (:cumulative-latency-ns inner-layer#))
                                                                               cumulative-latency-ns#))
                                        :error?             error?#}))
                 [])))))))