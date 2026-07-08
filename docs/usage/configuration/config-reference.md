---
layout: default
title: ValiPop Configuration Reference
markdown: kramdown
---

# ValiPop Configuration Reference

These are all the configuration options supported by ValiPop. Options suffixed with a '*' are required.

**Configuration Options**

- [Locations](#locations)
    - [`group_name`](#group_name)
    - [`results_save_location`](#results_save_location)
    - [`summary_results_save_location`](#summary_results_save_location)
    - [`input_distributions_path`*](#input_distributions_path)
    - [`project_location`](#project_location)
- [Dates and Periods](#dates-and-periods)
    - [`initialisation_start`*](#initialisation_start)
    - [`simulation_start`*](#simulation_start)
    - [`simulation_end`*](#simulation_end)
    - [`simulation_time_step`](#simulation_time_step)
    - [`min_birth_spacing`](#min_birth_spacing)
    - [`min_gestation_period`](#min_gestation_period)
    - [`distribution_granularity`](#distribution_granularity)
- [Simulation Factors](#simulation-factors)
    - [`target_initial_population_size`*](#target_initial_population_size)
    - [`initialisation_birth_rate`](#initialisation_birth_rate)
    - [`initialisation_death_rate`](#initialisation_death_rate)
    - [`recovery-factor`](#recovery-factor)
    - [`proportional_recovery_factor`](#proportional_recovery_factor)
- [Results](#results)
    - [`record_export_format`](#record_export_format)
    - [`population_export_format`](#record_export_format)
    - [`output_table`](#output_table)
    - [`contingency_table_stepback`](#contingency_table_stepback)
    - [`contingency_table_precision`](#contingency_table_precision)
- [Miscellaneous](#miscellanious)
    - [`deterministic`](#deterministic)
    - [`seed`](#seed)
    - [`binomial_sampling`](#binomial_sampling)
    - [`over_sized_geography_factor`](#over_sized_geography_factor)

***

## Locations

<dl>


<dt>
<a name="group_name">
<code>group_name</code>
</a>
</dt>

<dd markdown="1">
A name used for a grouping of runs. Must be a valid name for files and directories. 

Results of a specific run are written to

`<results_save_location>/<group_name>/<timestamp>`

`timestamp` represents the datetime the runs was executed at in the form `yyyy-mm-ddThh-mm-ss-sss`.

Defaults to `default`
</dd>

<dt>
<a name="results_save_location">
<code>results_save_location</code>
</a>
</dt>

<dd markdown="1">
Path to the root results directory of runs.

Defaults to `results/`.
</dd>

<dt>
<a name="summary_results_save_location">
<code>summary_results_save_location</code>
</a>
</dt>

<dd markdown="1">
Path to the directory where summarisations of runs are written.

A summary is a CSV file where each row is represented as a run. Each row contains information about the run configuration and results.

There is a global summary file shared by all runs at 

```<summary_results_save_location>/global-results-summary.csv```


There is local summary file shared by all runs of the same run purpose at

 ```<summary_results_save_location>/<group_name>/<group_name>-results-summary.csv```

Defaults to `results/`.
</dd>

<dt>
<a name="input_distributions_path">
<code>input_distributions_path</code>
</a>
</dt>

<dd markdown="1">
Path to the input distribution directory.

This is required.
</dd>

<dt>
<a name="project_location">
<code>project_location</code>
</a>
</dt>

<dd markdown="1">
Path to the project directory.

Defaults to `.` (current directory).
</dd>

</dl>

## Dates and Periods

<dl>

<dt>
<a name="initialisation_start">
<code>initialisation_start</code>
</a>
</dt>

<dd markdown="1">
The start date of the initialisation phase, where an initial population is generated and simulated until `simulation_start`. The period between `initialisation_start` and `simulation_start` must be at least 150 years to allow enough time for the preliminary population to settle.

At `initialisation_start` an initial population is first spawned, of which its size is based on [`initialisation_birth_rate`](#initialisation_birth_rate) and [`initialisation_death_rate`](#initialisation_death_rate), and the duration from [`simulation_start`](#simulation_start). The population is then simulated regularly until [`simulation_start`](#simulation_start). 

This is required.
</dd>

<dt>
<a name="simulation_start">
<code>simulation_start</code>
</a>
</dt>

<dd markdown="1">
The start date of the main phase, where records of events occurring between `simulation_start` until [`simulation_end`](#simulation_end) will be recorded.

This is required.
</dd>

<dt>
<a name="simulation_end">
<code>simulation_end</code>
</a>
</dt>

<dd markdown="1">
The end date of the main phase, and end of the simulation in total.

This is required.
</dd>

<dt>
<a name="simulation_time_step">
<code>simulation_time_step</code>
</a>
</dt>

<dd markdown="1">
The time interval used for each simulation step. This is a Java period string of the form `P<year>Y<month>M<day>D`.

Defaults to `P1Y` (1 year).
</dd>

<dt>
<a name="min_birth_spacing">
<code>min_birth_spacing</code>
</a>
</dt>

<dd markdown="1">
The minimum time interval parents must wait since their last child before having another child. This is a Java period string of the form `P<year>Y<month>M<day>D`.

Defaults to `P147D` (147 days).
</dd>

<dt>
<a name="min_gestation_period">
<code>min_gestation_period</code>
</a>
</dt>

<dd markdown="1">
The minimum time interval the child must have been conceived at before their birth. This is a Java period string of the form `P<year>Y<month>M<day>D`.

Defaults to `P147D` (147 days).
</dd>

<dt>
<a name="distribution_granularity">
<code>distribution_granularity</code>
</a>
</dt>

<dd markdown="1">
The time intervals for which the given input distributions are divided into between [`initialisation_start`](#initialisation_start) and [`simulation_end`](#simulation_end). Input distributions for the same properties over multiple different years are the separated into their respective time_intervals. This is a Java period string of the form `P<year>Y<month>M<day>D`.

Defaults to `P1Y` (1 year).
</dd>

</dl>

## Simulation Factors

<dl>

<dt>
<a name="target_initial_population_size">
<code>target_initial_population_size</code>
</a>
</dt>

<dd markdown="1">
The desired population size at [`simulation_start`](#simulation_start). The initialisation phase will aim to generate an initial population of this size from [`initialisation_start`](#initialisation_start) until [`simulation_start`](#simulation_start).

This is required.
</dd>


<dt>
<a name="initialisation_birth_rate">
<code>initialisation_birth_rate</code>
</a>
</dt>

<dd markdown="1">
The flat birth rate used for the initial population between [`initialisation_start`](#initialisation_start) and [`simulation_start`](#simulation_start). It represents the percentage increase of the population in one time step as a decimal. 

Defaults to `0.133`.
</dd>

<dt>
<a name="initialisation_death_rate">
<code>initialisation_death_rate</code>
</a>
</dt>

<dd markdown="1">
The flat death rate used for the initial population between [`initialisation_start`](#initialisation_start) and [`simulation_start`](simulation_start). It represents the percentage decrease of the population in one time step as a decimal. 

Defaults to `0.122`.
</dd>

<dt>
<a name="recovery_factor">
<code>recovery_factor</code>
</a>
</dt>

<dd markdown="1">
A multiplier determining how strongly the simulation should compensate for deviations from given one dimensional input distributions

Defaults to `1`.
</dd>

<dt>
<a name="proportional_recovery_factor">
<code>proportional_recovery_factor</code>
</a>
</dt>

<dd markdown="1">
A multiplier determining how strongly the simulation should compensate for deviations from given two dimensional input distributions

Defaults to `1`.
</dd>

</dl>

## Results

<dl>

<dt>
<a name="record_export_format">
<code>record_export_format</code>
</a>
</dt>

<dd markdown="1">
The output format of the target population records. Can be one of:

- `NONE`          : Does not generate.
- `TD`            : Custom record format created by Tom Dalton.
- `DS`            : Record format used by Digitising Scotland.
- `EG_SKYE`       : Subset of the `DS` format.
- `VIS_PROCESSING`: Simplified record format used by Digitising Scotland.

Defaults to `NONE`.
</dd>

<dt>
<a name="population_export_format">
<code>population_export_format</code>
</a>
</dt>

<dd markdown="1">
The output format of the target population graphic. Can be one of:

- `NONE`: Does not generate.
- `GRAPHVIZ`: a Graphviz `.dot` file to render a family tree graph
- `GEDCOM`: a GEDCOM family tree file
- `GEOJSON`: a Geojoson file showing the birth adresses of each person

Defaults to `None`.
</dd>

<dt>
<a name="output_table">
<code>output_table</code>
</a>
</dt>

<dd markdown="1">
When `true`, this creates contingency tables required by the population analysis.

When `false`, the contingency tables are not created and the population analysis is skipped.

A contingency table is a collection of expected and actual frequencies for events. Contingency tables for birth orders, multiple births, partnership, deaths, and separations are generated.

Defaults to `true`.
</dd>

<dt>
<a name="contingency_table_stepback">
<code>contingency_table_stepback</code>
</a>
</dt>

<dd markdown="1">
The stepback used in the contingency table calculation.

Defaults to `1`.
</dd>

<dt>
<a name="contingency_table_precision">
<code>contingency_table_precision</code>
</a>
</dt>

<dd markdown="1">
The precision used in the contingency table calculation.

Defaults to `1E-66`.
</dd>

</dl>

## Miscellaneous

<dl>

<dt>
<a name="deterministic">
<code>deterministic</code>
</a>
</dt>

<dd markdown="1">
When `true`, the program seeds its random generator with the value of [`seed`](#seed). This will yield the same result for every run using the same [`seed`](#seed).

When `false`, it will use the system time. This will likely yield different results on every run.

Defaults to `false`.
</dd>


<dt>
<a name="seed">
<code>seed</code>
</a>
</dt>

<dd markdown="1">
The value used to seed random generator. This will be ignored if [`deterministic = false`](#deterministic).

Defaults to `56854687`.
</dd>

<dt>
<a name="binomial_sampling">
<code>binomial_sampling</code>
</a>
</dt>

<dd markdown="1">
When `true`, counts determined by the given input distributions are sampled from binomial distributions.

When `false`, counts determined by the given input distributions are sampled from normal distributions.

Defaults to `true`.
</dd>

<dt>
<a name="over_sized_geography_factor">
<code>over_sized_geography_factor</code>
</a>
</dt>

<dd markdown="1">
The multiplier applied when determining the number of house addresses in a given area.

This is used for determining moving addresses and partnering.

Defaults to `1`.
</dd>

</dl>
