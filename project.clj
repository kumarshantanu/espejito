(defproject espejito "0.1.0"
  :description "Inter-layer latency finder for single-threaded processing"
  :url "https://github.com/kumarshantanu/espejito"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :global-vars {*warn-on-reflection* true
                *assert* true}
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.7.0-RC1"]]
                   :global-vars {*unchecked-math* :warn-on-boxed}}})
