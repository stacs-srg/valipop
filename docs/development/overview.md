---
layout: default
title: Valipop Overview
markdown: kramdown
---

# Valipop Overview

Valipop has there main stages during execution, population simulation, validation, and result generation.

### Population Simulation

This simulates a population with the given configuration and user-provided distributions. First the **preliminary population** is spawned at [`tS`](../usage/configuration/config-reference.md#tS) time and simulated to achieve the population size [`t0_pop_size`](../usage/configuration/config-reference.md#t0_pop_size) at [`t0`](../usage/configuration/config-reference.md#t0) time, which is then used to simulate the **target population** until the [`tE`](../usage/configuration/config-reference.md#tE) end time. Once the simulation is complete, the **preliminary population** is discarded

### Population Validation

Once the simulation of the target population is complete, the target population is analysed compared to the given input distributions. The analysis gives a score for the target population based on how statistically different it is from the given distributions. A lower score means the population and the distribution closely match, which is desired.

### Population Results

After the analysis, the analysis results, as well as some general statistics on the target population, are written to the results. Records and graphs, if specified, are also generated from the target population.
