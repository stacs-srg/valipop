---
layout: default
title: ValiPop Results
markdown: kramdown
---

# Valipop Results

All results of running Valipop are written to a single directory. The directory is saved in the following path structure

```
<results_save_location>/<run_purpose>/<datetime>/
```

[`results_save_location`](configuration/config-reference.md#results_save_location) and [`run_purpose`](configuration/config-reference.md#run_purpose) can be specified in the [config file](configuration/index.md) and `datetime` represents the datetime when valipop was executed in the form  `yyyy-mm-ddThh-mm-ss-sss`.

Valipop will create the directory structure for the results if it does not exist already.

## Result Structure

The directory structure of the results of running valipop looks like the following:

```
<results_path>/<run_purpose>/<datetime>/
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

<dl>

<dt>
<a name="analysisr">
<code>analysis.R</code>
</a>
</dt>

<dd markdown="1">
This file is the analysis script executed to validate the simulated population with the given statistics.
</dd>

<dt>
<a name="detailed-results-datetimetxt">
<code>detailed-results-&lt;datetime&gt;.txt</code>
</a>
</dt>

<dd markdown="1">
This file is generated once the model and analysis has completed. It provides additional statistics on the simulated model such as fertility and death rates, number of remarriages, population sizes, and average children per marriage.
</dd>

<dt>
<a name="dump">
<code>dump/</code>
</a>
</dt>

<dd markdown="1">
The dump directory contains bulk information used for debugging.
</dd>

<dt>
<a name="graphs">
<code>graphs/</code>
</a>
</dt>

<dd markdown="1">
The graph directory contains any graphs generated once the model and analysis have completed. The type of graph generated can be specified in the configuration.
</dd>


<dt>
<a name="log">
<code>log/</code>
</a>
</dt>

<dd markdown="1">
The log directory contains files which gives more details about the model simulation than in standard output
</dd>

<dt>
<a name="records">
<code>records/</code>
</a>
</dt>

<dd markdown="1">
The records directory contains any records generated once the model and analysis have completed. The record format generated can be specified in the configuration. Generally only birth, death, and marriage records are recorded among the generated population.
</dd>

<dt>
<a name="tables">
<code>tables/</code>
</a>
</dt>

<dd markdown="1">
The tables directory contains contigency tables on birth, death, partnership, and separation. They are used by the analysis to validate the simulated population with the given statistics.
</dd>

</dl>
