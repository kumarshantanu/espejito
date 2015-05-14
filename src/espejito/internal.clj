(ns espejito.internal)


(def ^:constant nanos-to-seconds 1000000000)


(def ^:constant nanos-to-millis 1000000)


(def ^:constant nanos-to-micros 1000)


(defn human-latency
  "Convert nano-second latency to human-readable form"
  [nanos]
  (cond
    (> nanos nanos-to-seconds) (str (double (/ nanos nanos-to-seconds)) "s")
    (> nanos nanos-to-millis)  (str (double (/ nanos nanos-to-millis)) "ms")
    (> nanos nanos-to-micros)  (str (double (/ nanos nanos-to-micros)) "us")
    :otherwise (str nanos "ns")))