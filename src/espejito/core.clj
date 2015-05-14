(ns espejito.core)


(def ^:dynamic *metrics* nil)


(defmacro measure
  "Use this at layer boundaries in code. Not recommended for tight loops - may cause out-of-memory situation!"
  [name & body]
  `(if *metrics*
     (let [start# (System/nanoTime)]
       (try
         ~@body
         (finally
           (conj! *metrics* [~name (- (System/nanoTime) start#)]))))
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
               (reduce (fn [result# [name# cumulative-latency-ns#]]
                         (conj result# {:name name#
                                        :cumulative-latency-ns cumulative-latency-ns#
                                        :individual-latency-ns (if-let [inner-layer# (last result#)]
                                                                 (- cumulative-latency-ns#
                                                                   (:cumulative-latency-ns inner-layer#))
                                                                 cumulative-latency-ns#)}))
                 [])))))))