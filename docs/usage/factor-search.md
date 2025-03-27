---
layout: default
title: Valipop Factor Search
markdown: kramdown
---

# Tuning Valipop with Factor Search

This explains how to use Valipop Factor Search in the case that population your validation score is too high.

## Recovery Factors

Once the target population has been simulated, the validation phase will determine how similar it is to the input distributions. Due to the inherent randomness of population simulation, sometimes the population may differ noticably from the input distributions, indicated by a high validation score.

Fortunately, Valipop provides two configuration options which dynamically compensates for deviations from the input distributions during the simulation runtime. [`recovery_factor`](configuration/config-reference.md#recovery_factor) compensates for deviations from one dimensional input distributions. [`proportional_recovery_factor`](configuration/config-reference.md#proportional_recovery_factor) compensates for deviations from two dimensional input distributions. The larger the value, the more strictly Valipop corrects deviations, where `0` means no corrections are done during the simulation. The default values for both these factors is `1`, which is typically enough to ensure most populations remain close to the input distributions.

## Factor search

In the unlikely case where more fine tuned recovery factors are needed, the Valipop repository provides a factor search program to identify effective values for [`recovery_factor`](configuration/config-reference.md#recovery_factor) and [`proportional_recovery_factor`](configuration/config-reference.md#proportional_recovery_factor).

The factor search program takes a series of configuration properties, and a list of recovery factors to test, and will generate configurations to simulate the population with. It will then attempt to simulate each population in parallel using [Apache Spark](https://spark.apache.org/). The validation scores of each combination of factors can then be observed in the results summary file to determine the best combination.

The program accepts 10 arguments:

1. The path to the input distributions ([`var_data_files`](configuration/config-reference.md#var_data_files))
2. The starting population size ([`t0_pop_size`](configuration/config-reference.md#t0_pop_size))
3. The run purpose ([`run_purpose`](configuration/config-reference.md#run_purpose))
4. The number of runs to test per configuration.
5. A comma separated list of the recovery factors to test  (Such as `0,0.5,1.0`)
6. A comma separated list of the proportional recovery factors to test  (Such as `0,0.5,1.0`)
7. The results directory ([`results_save_location`](configuration/config-reference.md#results_save_location))
8. The summary results directory ([`summary_results_save_location`](configuration/config-reference.md#summaryresults_save_location))
9. A comma separated list of contigency table precisions to test ([`ct_tree_precision`](configuration/config-reference.md#ct_tree_precision))
10. The project path ([`project_location`](configuration/config-reference.md#project_location))

### Running with Java

Factor search can be run on your local computer using the [Valipop Jar](execution/java.md#installing-the-jar-file). It will require the [dependencies of the Valipop JAR](execution/java.md#dependencies), and will additionally require [Apache Spark](https://spark.apache.org/) installed.

The JAR may then be passed to the `spark-submit` included with the Spark installation with the reqiured argument

`--class uk.ac.standrews.cs.valipop.implementations.DistributedFactorSearch`

The following demonstrates this with additional machine-specific Spark configuration

```shell
# Windows/MacOs/Linux terminal
# (Windows may require all arguments to be on the same line)

spark/bin/spark-submit \
    --class uk.ac.standrews.cs.valipop.implementations.DistributedFactorSearch \
    --master "local[*]" \
    --driver-memory 24G \
    --conf spark.driver.host=localhost \
    --conf spark.driver.port=5055 \
    valipop.jar \
    src/main/resources/valipop/inputs/synthetic-scotland/ \
    10000 \
    distributed \
    1 \
    "0,0.5,1" \
    "0,0.5,1" \
    results \
    results \
    1E-66 \
    . 
```

The above example runs the factor search in parallel on the local machine.

- `--master "local[*]"` specifies to use all available local cores
- `--driver-memory 24G` specifies m
- `--conf spark.driver.host=localhost` and `--conf spark.driver.port=5055` specifies the address `localhost:5055` which can be visited to view the progress of the search.

Alternatively, an the address of a Spark compatible cluster manager can be given to `--master` to distribute the program across a networked cluster.

### Creating a cluster

[Read about the supported cluster manager types](https://spark.apache.org/docs/latest/cluster-overview.html#cluster-manager-types).

The Valipop repository provides some preconfigured Docker images to create a standalone Spark cluster. This includes a leader and worker image which contain the dependencies needed to run Valipop. These images can be installed by running the following commands

```sh
# Windows/MacOs/Linux terminal

docker pull ghcr.io/daniel5055/valipop-leader:master
docker pull ghcr.io/daniel5055/valipop-worker:master
```

The leader image takes two arguments when run:

1. The address name to bind to (defaults to `localhost`)
2. The port to run on (defaults to `23177`)

The worker image takes two arguments when run:

1. The full address of the leader (defaults to `localhost:23177`)
2. The address name to bind to for the worker (defaults to `localhost`)

These images may then be launched on different machines on the same network to establish the cluster with the following command

```sh
# Windows/MacOs/Linux terminal

# To run the leader
docker run ghcr.io/daniel5055/valipop-leader:master

# To run the worker
docker run ghcr.io/daniel5055/valipop-worker:master
```

The worker additionally runs with the following environmental variables set:

- `SPARK_WORKER_MEMORY=30G`: The memory allocated to the worker
- `SPARK_WORKER_CORES=12`: The number of cores available to the worker
- `SPARK_WORKER_INSTANCES=1`: The number of instances per worker

These can be overwritten during container execution using the `-e` option with `Docker`.


### Running with Docker

The Valipop repisitory also provides a Docker image to run the factor search. This can be installed with the following command

```sh
docker pull ghcr.io/daniel5055/valipop-search:master
```

The factor search image takes 12 arguments, the first two relate to the cluster management:

1. The address of the cluster to use (defaults to `local[*]`)
2. The address name to bind to for the runner (defaults to `localhost`)

The remaining 10 arguments are passed to the factor search and are described [earlier](#factor-search)

The image may be run with the following command

```sh
docker run ghcr.io/daniel5055/valipop-search:master
```

As docker runs isolated from you local machine, you may need to mount directories to give Docker access to them. [Read more about working with Docker](execution/docker.md#running-valipop).
