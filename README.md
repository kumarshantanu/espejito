# espejito

[![cljdoc badge](https://cljdoc.org/badge/espejito/espejito)](https://cljdoc.org/d/espejito/espejito)

A Clojure/ClojureScript library to find latency across measurement points in single-threaded call trees.

Sample output:

```
|           :name | :cumulative | :cumul-% | :individual | :indiv-% |            :thrown? |
|-----------------+-------------+----------+-------------+----------+---------------------|
| outer           |   357.82 ms | 100.00 % |  103.037 ms |  28.80 % |                     |
|   inner         |  204.407 ms |  57.13 % |  150.723 ms |  42.12 % | java.lang.Exception |
|     inner-most  |   53.684 ms |  15.00 % |   53.684 ms |  15.00 % |                     |
|   inner-sibling |   50.376 ms |  14.08 % |   50.376 ms |  14.08 % |                     |
```

## Usage

Leiningen coordinates: `[espejito "0.2.0"]`

Requires Clojure 1.7 or higher, ClojureScript 1.9 or higher

### Requiring namespace
```clojure
(:require [espejito.core :as e])
```

### Starting to monitor

Wrap your outer-most layer with the following call

```clojure
(e/report e/print-table
  ...) ; body of code
```

### Measure at every layer

```clojure
(e/measure "layer-name"
  ...) ; body of code to measure
```

Or

```clojure
;; f is the function to wrap with measurement
(e/wrap-measure f "function-name")
```

The measure calls can be spread across several namespaces. Make sure that the layer-name is unique for every
measurement point.

### Instrumentation support on the JVM

The following convenience API is available on the JVM:

```clojure
(require '[espejito.instrument :as ins])

;; to instrument functions for latency measurement
(doseq ["com.myapp/foo"
        "com.myapp/bar"
        "com.myapp/baz"]
  (ins/instrument-measure each))
```

Then the report generated using `(e/report e/print-table ...)` looks
something like the following:

```
|             :name |   :cumulative | :cumul-% |   :individual | :indiv-% | :thrown? |
|-------------------+---------------+----------+---------------+----------+----------|
| com.myapp/foo     | 606.254183 ms | 100.00 % | 301.916861 ms |  49.80 % |          |
|   com.myapp/bar   | 304.337322 ms |  50.20 % | 203.490408 ms |  33.57 % |          |
|     com.myapp/baz | 100.846914 ms |  16.63 % | 100.846914 ms |  16.63 % |          |
```

## Caveats

This library assumes single-threaded code path only. For new/other threads you must use separate `report` for those
threads.

## License

Copyright © 2015-2021 Shantanu Kumar

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
