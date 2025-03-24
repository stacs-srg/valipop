---
layout: default
title: Running with Docker 
markdown: kramdown
---

# Valipop with Docker Guide

This guide will walk you through installing and running Valipop in Docker with some sample configurations. This guide will also demonstrate how to interpret the results of running Valipop and how to configure it further.

## 1. Prerequisites

Before we begin, you will need to have installed the following tools on your system for this walkthrough:

- [Git](https://git-scm.com/)
- [Docker](https://www.oracle.com/uk/java/)

## 2. Installation

To run Valipop with Docker, you only need the image. However we will be running Valipop with some sample configuration which needs to be installed separately.

### 2.1. Installing the image

You can install the lastest valipop image by running the following command

```sh
# In a terminal (Windows/MacOs/Linux)

docker pull ghcr.io/daniel5055/valipop:master
```

To verify you have installed the image correctly, you can run the following command

```shell
# In a terminal (Windows/MacOs/Linux)

docker run ghcr.io/daniel5055/valipop:master
```

which should print the following message

```txt
No config file given as 1st arg
Incorrect arguments given
```

### 2.2. Installing the configuration

We will use the config and input files from the [Valipop repository](https://github.com/stacs-srg/population-model). To install the repository, run the following command

```shell
# In a terminal (Windows/MacOs/Linux)

git clone https://github.com/stacs-srg/population-model.git
```

Within the repository, we will use the following config file

`src/main/resources/valipop/config/scot/config.txt`

### 3. Execution

Docker runs everything in an isolated environment. To ensure we can handle Valipop's input and output, we need to link directories on the computer to directories within the container.

In our case, we will want to link the following directories to the container:

- `./src` to `/app/src`
  - To access the config file and input distributions
- `./results` to `/app/results`
  - To receiv the results

Therefore we can run the container with the following command to bind the directories

```sh
# For Windows
docker run -v .\src:/app/src -v .\results:/app/results ghcr.io/daniel5055/valipop:master /app/src/main/resources/valipop/config/scot/config.txt

# For MacOs/Linux
docker run -v ./src:/app/src -v ./results:/app/results ghcr.io/daniel5055/valipop:master /app/src/main/resources/valipop/config/scot/config.txt
```
By default, this will run Valipop will a starting population size of 1000 between the years 1855 to 1973. This will usually take under 5 minutes to run, and should eventually print something like the following:

```
2025/03/24 16:31:39.343 :: Generating birth records
Elapsed time: 00:00:00
2025/03/24 16:31:39.374 :: Generating death records
Elapsed time: 00:00:00
2025/03/24 16:31:39.405 :: Generating marriage records
Elapsed time: 00:00:00
Running command: Rscript results/default/2025-03-24T16-30-28-046/analysis.R /cs/home/db255/Documents/dev/valipop/results/default/2025-03-24T16-30-28-046 55
Error in geeglm(formula, id = idvar, data = in.data, corstr = constr) : 
  Model matrix is rank deficient; geeglm can not proceed

Error in geeglm(formula, id = idvar, data = in.data, corstr = constr) : 
  Model matrix is rank deficient; geeglm can not proceed

Result: 0
```

In the same directory as the Valipop directory, there should also now exist a `results/` directory with the generated population records. This specific run should be located in a directory like

```
results/example/2025-03-24T10-50-39-702/
```

but the datetime may be different.

[Read more about Valipop results.](../results.md)

### 3.1. Customisation

If you navigate to `src/main/resources/valipop/config/scot/config.txt` and open in it in a text editor, it should look like the following

```
#Properties File

var_data_files = src/main/resources/valipop/inputs/synthetic-scotland
tS = 1687-01-01
t0 = 1855-01-01
tE = 1973-01-01
t0_pop_size = 1000

output_record_format = TD
output_tables = true

results_save_location = results
run_purpose = example
```

You can make the following changes to the configuration file to alter Valipop's behaviour

- Change the starting population size with [`t0_pop_size`](../configuration/config-reference.md#t0_pop_size).
- Change the start and end data (as written in the record files) with [`t0`](../configuration/config-reference.md#t0) and [`tE`](../configuration/config-reference.md#tE).
- Change input distributions used with [`var_data_files`](../configuration/config-reference.md#var_data_files) ([See Gotchas](#32-gotchas)). [Read more about input distributions.](../configuration/input-reference.md). 
- Change output record format with [`output_record_format`](../configuration/config-reference.md#output_record_format).
- To disable the analysis portion of valipop, set [`output_tables`](../configuration/config-reference.md#output_tables) to `false`.
- Change the location of the result directory with [`results_save_location`](../configuration/config-reference.md#results_save_location) (You would need to change the Docker linking also, [see Gotchas](#32-gotchas)).
- Change the name of the run within the result directory with [`run_purpose`](../configuration/config-reference.md#run_purpose).

You may then save the changes and rerun the following command for different results.

```sh
# In a terminal (Windows/MacOs/Linux)

java -jar valipop.jar src/main/resources/valipop/config/scot/config.txt
```

[Read more about the configuration options.](../configuration/config-reference.md)

### 3.2 Gotchas

As Valipop is running inside a container, all paths refer to files inside the container. In our case, we linked the directories such that we can use the same paths on the computer and container, but this is not alwasy the case.
