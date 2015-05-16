# espejito

A Clojure library to find latency across measurement points in single-threaded processing.

Sample output:

```
|           :name | :cumulative-latency | :individual-latency |                   :thrown? |
|-----------------+---------------------+---------------------+----------------------------|
| outer           |             358.7ms |           102.222ms |                            |
|   inner         |           202.397ms |           150.353ms | java.lang.RuntimeException |
|     inner-most  |            52.044ms |            52.044ms |                            |
|   inner-sibling |            54.081ms |            54.081ms |                            |
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
