(defproject espejito "0.2.0-SNAPSHOT"
  :description "Inter-layer latency finder for single-threaded processing"
  :url "https://github.com/kumarshantanu/espejito"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :global-vars {*warn-on-reflection* true
                *assert* true}
  :profiles {:provided {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :dev {:dependencies [[stringer "0.3.0"]]}
             :c07 {:dependencies [[org.clojure/clojure "1.7.0"]]
                   :global-vars {*unchecked-math* :warn-on-boxed}}
             :c08 {:dependencies [[org.clojure/clojure "1.8.0"]]
                   :global-vars {*unchecked-math* :warn-on-boxed}}
             :c09 {:dependencies [[org.clojure/clojure "1.9.0"]]
                   :global-vars {*unchecked-math* :warn-on-boxed}}
             :c10 {:dependencies [[org.clojure/clojure "1.10.1"]]
                   :global-vars {*unchecked-math* :warn-on-boxed}}})
