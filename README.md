# espejito

A Clojure library to find latency across measurement points in single-threaded processing.

Sample output:

```
|           :name | :cumulative | :cu-percent | :individual | :in-percent |            :thrown? |
|-----------------+-------------+-------------+-------------+-------------+---------------------|
| outer           |   357.82 ms |     100.00% |  103.037 ms |      28.80% |                     |
|   inner         |  204.407 ms |      57.13% |  150.723 ms |      42.12% | java.lang.Exception |
|     inner-most  |   53.684 ms |      15.00% |   53.684 ms |      15.00% |                     |
|   inner-sibling |   50.376 ms |      14.08% |   50.376 ms |      14.08% |                     |
```

## Usage

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

The measure calls can be spread across several namespaces. Make sure that the layer-name is unique for every
measurement point.

## Caveats

This library assumes single-threaded code path only. For new/other threads you must use separate `report` for those
threads.

## License

Copyright © 2015 Shantanu Kumar (kumar.shantanu@gmail.com, shantanu.kumar@concur.com)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
