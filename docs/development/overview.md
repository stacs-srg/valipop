---
layout: default
title: Running with Java 
markdown: kramdown
---

# ValiPop Overview

ValiPop has three main stages during execution: population simulation, validation, and result generation.

### Population Simulation

This simulates a population with the given configuration and user-provided distributions. First the **preliminary
population** is spawned at [`initialisation_start`](../usage/configuration/config-reference.md#initialisation_start)
time and simulated to achieve the population size
[`target_initial_population_size`](../usage/configuration/config-reference.md#target_initial_population_size) at
[`simulation_start`](../usage/configuration/config-reference.md#simulation_start) time, which is then used to simulate
the **target population** until the [`simulation_end`](../usage/configuration/config-reference.md#simulation_end) end
time. Once the simulation is complete, the **preliminary population** is discarded

[Read more about the population simulation](simulation.md)

### Population Validation

Once the simulation of the target population is complete, the target population is analysed compared to the given input
distributions. The analysis gives a score for the target population based on how statistically different it is from the
given distributions. A lower score means the population and the distribution closely match, which is desired.

[Read more about the population validation](validation.md)

### Population Results

After the analysis, the analysis results, as well as some general statistics on the target population, are written to
the results. Records and graphs, if specified, are also generated from the target population.
