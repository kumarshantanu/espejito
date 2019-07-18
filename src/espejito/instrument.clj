;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns espejito.instrument
  (:require
   [clojure.string :as string]
   [espejito.core :as e]
   [espejito.internal :as i])
  (:import
   [java.io FileNotFoundException]))


(defn shorten-ns
  "Given a fully qualified var name, shorten the namespace portion for
  conciseness and readability and return the fully qualified short name."
  (^String [^String fq-var-name]
   (shorten-ns fq-var-name {}))
  (^String [^String fq-var-name {:keys [^long intact-token-count]
                                 :or {intact-token-count 2}}]
   (let [ns-name-pair (string/split fq-var-name #"/")]
     (if (= 2 (count ns-name-pair))
       (let [[ns-name var-name] ns-name-pair
             ns-tokens (string/split ns-name #"\.")
             ns-tcount (count ns-tokens)
             ns-inits  (->> ns-tokens
                            (mapv first)
                            (take (max (- ns-tcount intact-token-count) 0)))
             ns-suffix (->> ns-tokens
                            (take-last (min ns-tcount intact-token-count)))
             actual-ns (->> ns-suffix
                            (concat ns-inits)
                            (string/join \.))]
         (str actual-ns "/" var-name))
       fq-var-name))))


(defn resolve-var
  "Given a fully qualified var name, resolve the var object and return it."
  [^String fq-var-name]
  ;; ensure that var name is non-empty
  (when (empty? fq-var-name)
    (throw (ex-info "Empty var name")))
  ;; ensure that var name is correctly formatted
  (let [first-slash-at (.indexOf fq-var-name "/")
        last-slash-at  (.lastIndexOf fq-var-name "/")]
    (when-not (and (pos? first-slash-at)
                   (< last-slash-at (unchecked-dec (.length fq-var-name)))
                   (= last-slash-at first-slash-at))
      (throw (ex-info (str "Malformed fully-qualified var name: "
                           (pr-str fq-var-name))))))
  ;; ensure that the namespace is loaded
  (let [[ns-name var-name] (string/split fq-var-name #"/")]
    (try
      (require (symbol ns-name))
      (catch FileNotFoundException _
        (when-not (= "user" ns-name)  ; ignore implicit ns at the REPL
          (throw (ex-info (format "Cannot find ns '%s'" ns-name) {}))))))
  ;; find the var now
  (find-var (symbol fq-var-name)))


(defn instrument-measure
  "Given a fully qualified var name, instrument the var such that upon execution it participates in Espejito latency measurement."
  ([var-or-varname]
   (instrument-measure var-or-varname {}))
  ([var-or-varname {:keys [name-encoder]
                    :or {name-encoder shorten-ns}
                    :as options}]
   (let [[fq-name the-var] (cond
                            ;; already a var
                             (var? var-or-varname)
                             (let [m (meta var-or-varname)
                                   n (format "%s/%s"
                                             (ns-name (:ns m))
                                             (:name m))]
                               [n (resolve-var n)])
                             ;; a string
                             (string? var-or-varname)
                             [var-or-varname (resolve-var var-or-varname)]
                             ;; a symbol
                             (symbol? var-or-varname)
                             (instrument-measure (str var-or-varname))
                             ;; a keyword
                             (keyword? var-or-varname)
                             (instrument-measure (format "%s/%s"
                                                         (namespace var-or-varname)
                                                         (name var-or-varname)))
                             ;; or else
                             :otherwise
                             (i/expected "a var or a fully-qualified var name"
                                         var-or-varname))]
     (alter-var-root the-var (fn [f]
                               (fn espejito-instrumented [& args]
                                 (e/measure (name-encoder fq-name)
                                            (apply f args))))))))
