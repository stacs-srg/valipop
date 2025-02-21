---
layout: default
title: ValiPop Configuration Reference
markdown: kramdown
---

# Valipop Configuration Reference

These are all the configuration options supported by Valipop.

<code>
-----------------------------------------
-------------- Locations ----------------
-----------------------------------------

run_purpose =

    A name used for grouping of runs. Must be a valid name for files and directories. 
    Defaults to `default`
    
    Results of a specific run are written to `<results_save_location>/<run_purpose>/<timestamp>`. `timestamp` represents the datetime the runs was executed at in the form `yyyy-mm-ddThh-mm-ss-sss`.


results_save_location = 

    Path to the root results directory of runs.
    Defaults to `results/`.

    Results of a specific run are written to `<results_save_location>/<run_purpose>/<timestamp>`. `timestamp` represents the datetime the runs was executed at in the form `yyyy-mm-ddThh-mm-ss-sss`.


summary_results_save_location =

    Path to the directory where summarisations of runs are written.
    Defaults to `results/`
    
    A summary is a CSV file where each run is represented as a run. Each row contains information about the run configuration and results.

    There is a global summary file shared by all runs at `<summary_results_save_location>/global-results-summary.csv`.

    There is local summary file shared by all runs of the same run purpose at `<summary_results_save_location>/<run_purpose>/<run_purpose>-results-summary.csv`.

var_data_files =

    Path to the input distribution directory.
    This is required.


-----------------------------------------
---------- Simulation Factors -----------
-----------------------------------------

set_up_dr =

    The flat birth rate used for the initial population between `tS` and `t0`. It represents the percentage increase of the population in one time step as a decimal. 
    Defaults to `0.133`.

set_up_br =

    The flat death rate used for the initial population between `tS` and `t0`. It represents the percentage decrease of the population in one time step as a decimal. 
    Defaults to `0.122`.

recovery_factor =

    A multiplier determing how strongly the simulation should compensate for deviations from given one dimensional input distributions
    Defaults to `1`.

proportional_recovery_factor =

    A multiplier determing how strongly the simulation should compensate for deviations from given two dimensional input distributions
    Defaults to `1`.

-----------------------------------------
---------- Dates and Periods ------------
-----------------------------------------

tS =

    The start date of the initialisation phase, where an initial population is generated and simulated until `t0`. The duration between `t0` and `tS` must be greater than or equal to the greatest age specified in the ordered birth rates distribution.
    This is required.
    
    At `tS` an initial population is first spawned, of which its size is based on `set_up_br` and `set_up_dr`, and the duration from `t0`. The population is then simulated reguraly until `t0`. 

t0 =

    The start date of the main phase, where records of events occuring between `t0` until `tE` will be recoreded.
    This is required.

tE =

    The end date of the main phase, and end of the simulation in total.
    This is required.

simulation_time_step =

    The time interval used for each simulation step. This is a Java period string of the form `P<year>Y<month>M<day>D`.
    Defaults to `P1Y` (1 year).

min_birth_spacing =

    The minimum time interval parents must wait since their last child before having another child. This is a Java period string of the form `P<year>Y<month>M<day>D`.
    Defaults to `P147D` (147 days).

min_gestation period =

    The minimum time interval the child must have been concieved at before their birth. This is a Java period string of the form `P<year>Y<month>M<day>D`.
    Defaults to `P147D` (147 days).

input_width = 

    The time intervals for which the given input distributions are divided into between `tS` and `tE`. Input distributions for the same properties over multiple different years are the separated into thier respective time_intervals. This is a Java period string of the form `P<year>Y<month>M<day>D`.
    Defaults to `P1Y` (1 year).

-----------------------------------------
------------ Miscellanious --------------
-----------------------------------------

output_record_format =

    The output format of the target population records. Can be one of:
    
    - `NONE`          : Does not generate.
    - `TD`            : Custom record format created by Tom Dalton.
    - `DS`            : Record format used by Digitising Scotland.
    - `EG_SKYE`       : Subset of the `DS`.
    - `VIS_PROCESSING`: Simplified record format used by Digitising Scotland.

    Defaults to `NONE`.

output_graph_format =

    The output format of the target population graphic. Can be one of:

    - `NONE`: Does not generate.
    - `GRAPHVIZ`: a Graphviz `.dot` file to render a family tree graph
    - `GEDCOM`: a GEDCOM family tree file

    Defaults to `None`.

t0_pop_size =

    The desired population size at `t0`. The initialisation phase will aim to generate an initial population of this size from `tS` until `t0`.
    This is required.

deterministic = 

    When `true`, the program seeds its random generator with the value of `seed`. This will yield the same result for every run using the same `seed`.

    When `false`, it will use the system time. This will likely yield different results on every run.

    Defaults to `false`.


seed =

    The value used to seed random generator. This will be ignored if `deterministic = false`.
    Defaults to `56854687`

binomial_sampling =

    When `true`, counts determined by the given input distributions are sampled from binomial distributions.

    When `false`, counts determined by the given input distributions are sampled from normal distributions.

    Defaults to `true`.

over_sized_geography_factor =

    The multiplier applied when determining the number of house addresses in a given area.
    Defaults to `1`

    This is used for determining moving addresses and partnering.

output_table =

    When `true`, this creates contigency tables required by the population analysis.

    When `false`, the contigency tables are not created and the population analysis is skipped.

    A contigency table is a collection of expected and actual frequencies for events. Contigency tables for birth orders, multiple births, partnership, deaths, and separations are generated.

    Defaults to `true`.


ct_tree_stepback = 

    The stepback used in the contigency table calculation.

ct_tree_precision =

    The precision used in the contigency table calculation.
</code>
