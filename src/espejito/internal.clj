(ns espejito.internal)


(def ^:constant nanos-to-seconds 1000000000)


(def ^:constant nanos-to-millis 1000000)


(def ^:constant nanos-to-micros 1000)


(defn human-readable-latency
  "Convert nano-second latency to human-readable form"
  [^long nanos]
  (cond
    (> nanos ^long nanos-to-seconds) (str (double (/ nanos ^long nanos-to-seconds)) "s")
    (> nanos ^long nanos-to-millis)  (str (double (/ nanos ^long nanos-to-millis)) "ms")
    (> nanos ^long nanos-to-micros)  (str (double (/ nanos ^long nanos-to-micros)) "us")
    :otherwise (str nanos "ns")))


(def ^:cont max-name-width 60)


(def ^String padding (apply str (repeat max-name-width \space)))


(defn indent-name
  [^long level name]
  (-> (str (when (pos? level) (apply str (repeat (* 2 level) \space))) name padding)
    (subs 0 max-name-width)))


(declare collect-children-report)


(defn collect-child-report
  [transient-collector ^long level [name ^long cumulative-latency-ns error? children-metrics]]
  (let [indented-name (indent-name level name)]
    (conj! transient-collector
      {:name indented-name
       :cumulative-latency-ns cumulative-latency-ns
       :cumulative-latency (human-readable-latency cumulative-latency-ns)
       :individual-latency (human-readable-latency (- ^long cumulative-latency-ns
                                                     (let [^long children-latency-ns (->> children-metrics
                                                                                       (map second)
                                                                                       (reduce + 0))]
                                                       children-latency-ns)))
       :error?             error?}))
  (collect-children-report transient-collector (inc level) children-metrics))


(defn collect-children-report [transient-collector ^long level children-metrics]
  (doseq [each children-metrics]
    (collect-child-report transient-collector level each)))