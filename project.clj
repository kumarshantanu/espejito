(defproject espejito "0.1.1-SNAPSHOT"
  :description "Inter-layer latency finder for single-threaded processing"
  :url "https://github.com/kumarshantanu/espejito"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :global-vars {*warn-on-reflection* true
                *assert* true}
  :profiles {:provided {:dependencies [[org.clojure/clojure "1.5.1"]]}
             :dev {:dependencies [[stringer "0.3.0"]]}
             :c15 {:dependencies [[org.clojure/clojure "1.5.1"]]}
             :c16 {:dependencies [[org.clojure/clojure "1.6.0"]]}
             :c17 {:dependencies [[org.clojure/clojure "1.7.0"]]
                   :global-vars {*unchecked-math* :warn-on-boxed}}
             :c18 {:dependencies [[org.clojure/clojure "1.8.0"]]
                   :global-vars {*unchecked-math* :warn-on-boxed}}
             :c19 {:dependencies [[org.clojure/clojure "1.9.0-alpha13"]]
                   :global-vars {*unchecked-math* :warn-on-boxed}}})
