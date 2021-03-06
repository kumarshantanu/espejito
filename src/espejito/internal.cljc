;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns espejito.internal)


(defn expected
  "Throw illegal input exception citing `expectation` and what was `found` did not match. Optionally accept a predicate
  fn to test `found` before throwing the exception."
  ([expectation found]
   (throw (ex-info
           (str "Expected " expectation ", but found (" (pr-str (type found)) ") " (pr-str found))
           {:found found})))
  ([pred expectation found]
   (when-not (pred found)
     (expected expectation found))))


(defn percent
  ^double [^long numerator ^long denominator]
  (double (/ (* 100 numerator) denominator)))


(def ^:const nanos-to-seconds 1000000000)


(def ^:const nanos-to-millis 1000000)


(def ^:const nanos-to-micros 1000)


(defn nanos
  "Return duration in nanoseconds since Epoch, or since the supplied timestamp (in nanos)."
  (^long []
   #?(:cljs (unchecked-multiply (.getTime (js/Date.)) nanos-to-millis)
      :clj (System/nanoTime)))
  (^long [^long start]
   (unchecked-subtract (nanos) start)))


(defn human-readable-latency
  "Convert nano-second latency to human-readable form"
  [^long nanos]
  (cond
    (> nanos nanos-to-seconds) (str (double (/ nanos nanos-to-seconds)) " s")
    (> nanos nanos-to-millis)  (str (double (/ nanos nanos-to-millis)) " ms")
    (> nanos nanos-to-micros)  (str (double (/ nanos nanos-to-micros)) " µs")
    :otherwise (str nanos "ns")))


(def ^String padding (apply str (repeat 100 \space)))


(defn indent-name
  [^long max-name-width ^long level name]
  (-> (str (when (pos? level) (apply str (repeat (* 2 level) \space))) name padding)
    (subs 0 max-name-width)))


(declare collect-children-report)


(defn collect-child-report
  [transient-collector ^long level [name ^long cumulative-latency-ns thrown? children-metrics]]
  (conj! transient-collector
    {:name name
     :level level
     :cumulative-latency-ns cumulative-latency-ns
     :individual-latency-ns (unchecked-subtract ^long cumulative-latency-ns
                                                (let [^long children-latency-ns (->> children-metrics
                                                                                     (map second)
                                                                                     (reduce unchecked-add 0))]
                                                  children-latency-ns))
     :thrown? thrown?})
  (collect-children-report transient-collector (inc level) children-metrics))


(defn collect-children-report [transient-collector ^long level children-metrics]
  (doseq [each children-metrics]
    (collect-child-report transient-collector level each)))


(defn flatten-children-report
  [nested-metrics]
  (let [result (transient [])]
    (collect-children-report result 0 nested-metrics)
    (persistent! result)))