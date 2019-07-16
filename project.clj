(defproject espejito "0.2.0-SNAPSHOT"
  :description "Inter-layer latency finder for single-threaded processing"
  :url "https://github.com/kumarshantanu/espejito"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :global-vars {*warn-on-reflection* true
                *assert* true
                *unchecked-math* :warn-on-boxed}
  :profiles {:provided {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :c07 {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :c08 {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :c09 {:dependencies [[org.clojure/clojure "1.9.0"]]}
             :c10 {:dependencies [[org.clojure/clojure "1.10.1"]]}})
