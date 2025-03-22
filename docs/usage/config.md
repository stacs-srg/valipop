---
layout: default
title: ValiPop Configuration
markdown: kramdown
---

# Valipop Configuration

## The config File

The main source of configuration for `Valipop` is the config file. This a `.txt` file containing a collection of configuration options per line. A configuration option is of the form 

```<option> = <value>```

where `option` is the name of the configuration option and `value` is the value given. Comments are prefixed with `#` and are ignored by `valipop`.

This is an example of a simple configuration file:

```txt
# Properties File - for model v3 - Tom, Graham, Al

var_data_files = src/main/resources/valipop/inputs/synthetic-scotland
tS = 1687-01-01
t0 = 1855-01-01
tE = 1973-01-01
t0_pop_size = 1000
```

See [here](https://daniel5055.github.io/valipop/usage/config-reference.html) for all possible configuration options.

## The Input Distributions

Input distributions provide the probablities used to simulate all properties of the population.

### Directory Structure

The input distributions for a given population must be contained in a single directory, and this directory can be passed to Valipop with the `var_data_files` option in the config file.

The structure of the input distributions directory is shown in the following figure:

```
my-input-distribution/
│
├───annotations/
│   ├───female_forename/
│   ├───male_forename/
│   ├───surname/
│   ├───geography/
│   │
│   ├───occupation/
│   │   ├───change/
│   │   ├───female/
│   │   └───male/
|   |
│   └───migration/
│       ├───female_forename/
│       ├───male_forename/
│       ├───surname/
│       └───rate/
|       
├───birth/
│   ├───adulterous_birth/
│   ├───multiple_birth/
│   ├───ordered_birth/
│   └───ratio_birth/
│
├───death/
│   ├───females/
│   │   ├───cause/
│   │   └───lifetable/
│   │
│   └───males/
│       ├───cause/
│       └───lifetable/
│
└───relationships/
    ├───marriage/
    ├───partnering/
    └───separation/
```


### Input Distributions Files

Each end directory (directory without sub directories) represents a property of the population. Within an end directory is any number of input distribution files for that property, often for different years.

In the following example, the marriage property (which defines the proportion of parents that are married), contains three input distributions from different years.

```
└───relationships/
    └───marriage/
        ├───marriage_1938.txt
        ├───marriage_1953.txt
        └───marriage_1973.txt
```

Figure 3


**Input distribution files can have any name.**

Each input distribution of a property will apply for a period of time during the simulation. The length of the period is defined by the `input_width` option in the config file. Valipop will divide the given input distributions into these equal periods based on which input distribution is closest to the end time of that period.

For example, using the input distributions defined in Figure 3 in a simulation running from years 1900 to 2000 with an input width of 10 years. The input distributions will be divided over the following periods.

```
                       marriage_1953.txt
                         ┌─────┴─────┐
     marriage_1938.txt                   marriage_1973.txt
 ┌───────────┴───────────┐           ┌───────────┴───────────┐

1900  1910  1920  1930  1940  1950  1960  1970  1980  1990  2000
 ├─────┼─────┼─────┼─────┼─────┼─────┼─────┼─────┼─────┼─────┤
```

### Input Distribution Format

Each individual distribution file contains at least the following information at the start of the file.

```
YEAR	<year>
POPULATION	<location>
SOURCE	<source>
LABELS	<tab-separated column labels>
DATA
...
```

Each field and value must be separated by a tab character. You may include your own fields, but only the following are read by Valipop:

- `YEAR` specifies what year the distribution applies to. 
- `POPULATION` specifies what population the distribution is based on.
- `SOURCE` specifies where the distribution was acquired
- `LABELS` specifies the labels of each column in the data, separated by tab characters.

Everything included after the `DATA` field will be recorded as the input distribution data.

The format of the data depends on the property the distribution is for, but all formats use tab characters to separate values.
