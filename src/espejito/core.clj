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
  "Print the report using clojure.pprint/print-table"
  ([nested-metrics]
    (print-table 50 nested-metrics))
  ([^long name-column-width nested-metrics]
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
        (pp/print-table [:name :cumulative :cumul-% :individual :indiv-% :thrown?])))
    (println "\nNo data to report!"))))


(defmacro report
  "Use this at the outermost periphery of the code."
  [f & body]
  `(binding [*metrics* (transient [])]
     (try
       ~@body
       (finally
         (~f (persistent! *metrics*))))))