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

See [here](/usage/config/reference) to see all possible configuration options.

## Input Distribution