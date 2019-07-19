# TODO and History

## TODO

- [TODO] Support for measuring Java code
- [TODO] Support for carrying context into child threads


## 0.2.0-alpha2 / 2019-July-18

- Drop support for Clojure 1.5 and 1.6
- Add ClojureScript support
- Add `espejito.core/wrap-measure` to wrap functions for measurement
- Add `espejito.instrument` namespace (instrumentation on JVM)
  - `shorten-ns`  (utility)
  - `resolve-var` (utility)
  - `instrument-measure` (instrument)


## 0.1.1 / 2016-October-13

- Support for pluggable table printer in `espejito.core/print-table`


## 0.1.0 / 2015-June-02

- Measurement API for single-threaded execution
- Reporting API for metrics collection
