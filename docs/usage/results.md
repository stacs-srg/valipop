---
layout: default
title: ValiPop
markdown: kramdown
---

# _Valipop_ Results

All results of running Valipop are written to a single directory. The directory is saved in the following path structure

```
<results_path>/<run_purpose>/<current_time>/
```

`results_path` and `run_purpose` can be specified in the [configuration](/usage/config.md) and `current_time` represents the timestamp when valipop was executed.

Valipop will create the directory structure for the results if it does not exist already.

## Result Structure

The directory structure of the results of running valipop looks like the following:

```
├───analysis.R
├───detailed-results-<datetime>.txt
│
├───dump/
│   └───order.csv
│
├───graphs/
│   └───graph.png
│
├───log/
│   └───trace.txt
│
├───records/
│   ├───birth_records.csv
│   ├───death_records.csv
│   └───marriage_records.csv
│
└───tables/
    ├───death-CT.csv
    ├───mb-CT.csv
    ├───ob-CT.csv
    ├───part-CT.csv
    └───sep-CT.csv
```

### `log/`

The log directory contains files which gives more details about the model simulation than in standard output

### `graph/`

The graph directory contains any graphs generated once the model and analysis have completed. The type of graph generated can be specified in the configuration.

### `records/`

The records directory contains any records generated once the model and analysis have completed. The record format generated can be specified in the configuration. Generally only birth, death, and marriage records are recorded among the generated population.

### `tables/`

The tables directory contains contigency tables on birth, death, partnership, and separation. They are used by the analysis to validate the simulated population with the given statistics.

### `dump/`

The dump directory contains bulk information used for debugging.

### `detailed-results-<datetime>.txt`

This file is generated once the model and analysis has completed. It provides additional statistics on the simulated model such as fertility and death rates, number of remarriages, population sizes, and average children per marriage.

### `analysis.R`

This file is the analysis script executed to validate the simulated population with the given statistics.

