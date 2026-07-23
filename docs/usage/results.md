---
layout: default
title: ValiPop Results
markdown: kramdown
---

# ValiPop Results

## Validation Results

Running ValiPop with [`contingency_table_export=true`](configuration/config-reference.md#contingency_table_export) will enable the validation phase of ValiPop. This will analyse the simulated target population to determine how similar it is to given input distributions. The result of the validation is written to the terminal output as the `Validation score`. The lower the score, the more similar the population is to the given input distributions, which is desired. 0 is the best achievable score. 

The following text shows sample terminal output from running ValiPop with Validation:

```
Running simulation with /app/src/test/resources/valipop/config/config-1.txt
Writing contingency tables
Writing records
2025/03/26 15:03:54.292 :: Generating birth records
Elapsed time: 00:00:00
2025/03/26 15:03:54.332 :: Generating death records
Elapsed time: 00:00:00
2025/03/26 15:03:54.367 :: Generating marriage records
Elapsed time: 00:00:00
Writing graph
Running validation with command: Rscript /app/results/test/2025-03-26T14-26-12-324/analysis.R /app/results/test/2025-03-26T14-26-12-324 50 1854 1973
Warning message:
In value[[3L]](cond) : Population size too small for partnering analysis
Validation score: 0.0 (good)
```

Notably, there may be warning messages, like in the terminal output above, that say the population is too small for some types of analysis. This means that some types of analysis may not be included in the validation score due to the lack of data to draw a meaningful conclusion. Generally population sizes of 10,000 and above are enough for all types of analysis.

## Simulation Results

All simulation results of running ValiPop are written to a single directory. The directory is saved in the following path structure

```
<results_save_location>/<group_name>/<datetime>/
```

[`results_save_location`](configuration/config-reference.md#results_save_location) and [`group_name`](configuration/config-reference.md#group_name) can be specified in the [config file](configuration/index.md) and `datetime` represents the datetime when ValiPop was executed in the form  `yyyy-mm-ddThh-mm-ss-sss`.

ValiPop will create the directory structure for the results if it does not exist already.

### Result Structure

The files output from a simulation run are structured as follows:

```
<results_path>/<group_name>/<datetime>/
├───input.config
│
├───log/
│   └───log.txt
│
├───population/
│   └───population.ged
│
├───records/
│   ├───birth-records.csv
│   ├───death-records.csv
│   └───marriage-records.csv
│
├───statistics.txt
│
└───tables/
    ├───birth-contingency-table.csv
    ├───death-contingency-table.csv
    ├───multiple-birth-contingency-table.csv
    ├───partnership-contingency-table.csv
    └───separation-contingency-table.csv
```

<dl>

<dt>
<code>input.config</code>
</dt>

<dd>
This is a copy of the configuration file for the run.
</dd>

<dt>
<code>log/</code>
</dt>

<dd>
The <em>log</em> directory contains files giving more detail about the simulation run.
</dd>

<dt>
<code>population/</code>
</dt>

<dd>
The <em>population</em> directory contains the exported representation of the simulated population, if specified in the configuration file.
</dd>

<dt>
<code>records/</code>
</dt>

<dd>
The <em>records</em> directory contains the records generated from the simulated population, if specified
in the configuration file.
</dd>

<dt>
<code>statistics.txt</code>
</dt>

<dd>
This file provides additional statistics on the simulated population, including fertility, death and remarriage rates, and family size distributions.
</dd>

<dt>
<code>tables/</code>
</dt>

<dd>
The <em>tables</em> directory contains contingency tables for birth, death, partnership, and separation. They are analysed during validation of the simulated population.
</dd>

</dl>
