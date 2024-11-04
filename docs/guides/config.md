# _ValiPop_ Configuration

## Config File

The main source of configuration for `Valipop` is the config file. This a text file containing a collection of assignments of values to config options. An assignment is of the form `<option> = <value>`. Comments are prefixed with `#` and are ignored by `valipop`. Below detail the possible options are 

### File Paths

`var_data_files`

- Path to the input directory

`results_save_location`

- Path to the results directory where data from the simulation is written. Defaults to `results/`.

`summary_results_save_location`

- Path to the directory where summarising data about runs are contained. Defaults to `results/`.

### Distribution Factors

`set_up_dr`

- The birth rate used in calculating the initial population size at `tS`.

`set_up_br`

- The death rate used in calculating the initial population size at `tS`.

`birth_factor` = $b$

- This options takes a float greater or equal to 0. It reduces the number of births that occur at every timestep. It gives a probability of $b/\ceil{b}$ to reduce the number of births that occured in a timestep by $\ceil{b}$

`death_factor`

- This option takes a float $d$ between 0 and 1 inclusive. $d$ is the probability of reducing the number of deaths to occur by 1 at each timestep.

`recovery_factor`

- ??How quickly the model should try to compensate for deviations from the given distribution?

`proportional_recovery_factor`

- ??


### Dates and Periods

`tS`

- Represents the start date of the initialisation period. This is where an initial population is generated and simulated until `t0`. This initial simulation can spawn orphaned childrened when needed to ideally reach the target population size `t0_pop_size`.

`t0`

- Represents the start date of the main simulation. The people born from the initialised popupation and onwards are recorded by the simulation until the end datae `tE`.

`tE`

- Represents the end date of the main simulation. 

`simulation_time_step`

- The unit time step used in the simulation. The period is represented as a Java period strig of the form `P<year>Y<month>M<day>D`.

`min_birth_spacing`

- ??Something to do with period of pregnancy

`min_gestation period`

- ??Also Something do with period of pregnancy

`output_record_format`

- This specifies the output format of the population records. These records represent the simulated population by representing each birth, death, and marriage between `t0` and `tE` in tabular form. 

### Other

`input_width`

- Represents the smallest period for which distributions in input directory cover. If there are distributions for the same statistics for multiple different years, then those distributions will be divided among periods of the input width.

`t0_pop_size`

- The desired initial population size at the start time of the main simulation, `t0`. The initialisation period will try to generate a population of this size between `tS` and `t0` but it may not be exact due to randomness in the generation.

`deterministic`

- Whether the simulation uses a set seed or not for the randomness generator. When set to false, the current time is used as a seed, meaning you may get different results when rerunning the model. When set to true, it will use a set seed, meaning you will get the same result from running the model multiple times.

`seed`

- The seed to use for the randomness generator. This is ignored if `deterministic` is set to false

`binomial_sampling`

- When true, counts determined by the input distribution are sampled from a binomial distribution.

`run_purpose`

- A name used to group runs. Runs will be saved with a timestamp in a directory named with the run purpose, within the results directory.

`output_table`

- When true, this creates contigency tables which are used by the validation program to confirm the simulated population conforms to the given distributions. When set to false, these tables are not created and the validation program will not execute. The contigency tables represent the expected and actual frequencies of the birth orders, multiple births, partnerships, deaths, and separations.

`over_sized_geography_factor`

- ??

`ct_tree_stepback`

- ??

`ct_tree_precision`

- ??

## Input Structure
