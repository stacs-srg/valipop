---
layout: default
title: ValiPop Configuration
markdown: kramdown
---

# ValiPop Configuration

## The config File

The main source of configuration for ValiPop is the config file. This a `.txt` file containing a collection of configuration options per line. A configuration option is of the form 

```<option> = <value>```

where `option` is the name of the configuration option and `value` is the value given. Comments are prefixed with `#` and are ignored by ValiPop.

This is an example of a minimal configuration file:

```txt
# Properties File

var_data_files = src/main/resources/valipop/inputs/synthetic-scotland
tS = 1687-01-01
t0 = 1855-01-01
tE = 1973-01-01
t0_pop_size = 1000
```

See the [config reference](config-reference.md) for all possible configuration options.

## The Input Distributions

Input distributions provide the probabilities used to simulate all properties of the population. The collection of input distributions for a population should be contained within a single directory. The path to this directory can be passed to ValiPop with the [`var_data_files`](config-reference#var_data_files) option in the config file.

See the [input distribution reference](input-reference.md) for the structure and format of all input distributions.
