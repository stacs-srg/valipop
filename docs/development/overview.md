---
layout: default
title: Valipop Overview
markdown: kramdown
---

# Valipop Overview

Valipop has there main stages during execution, population simulation, validation, and result generation.

### Population Simulation

This simulates a population with the given configuration and user-provided distributions. First a **preliminary population** is generated up to the given starting population size from `tS` time to `t0` time, which is used to simulate the **target population** until the `tE` end time. Once the simulation is complete, the **preliminary population** is discarded

### Population Validation

Once the simulation of the target population is complete, the target population is analysed compared to the given distributions. The analysis gives a score for the target population based on how statistically different it is from the given distributions. A lower score means the population and the distribution closely match, which is desired.

### Population Results

After the analysis, the analysis results, as well as some general statistics on the target population, are written to the results. Records and graphs, if specified, are also generated from the target population.
