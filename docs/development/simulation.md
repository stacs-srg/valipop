---
layout: default
title: Valipop Overview
markdown: kramdown
---

# Valipop Population Simulation

## Preliminary population

### Preliminary population size

The inital size of preliminary population at [`tS`](../usage/configuration/config-reference.md#tS) is calculated by working backwards from [`t0_pop_size`](../usage/configuration/config-reference.md#t0_pop_size). Valipop uses the setup birth rate [`set_up_br`](../usage/configuration/config-reference.md#set_up_br) and setup death rate [`set_up_dr`](../usage/configuration/config-reference.md#set_up_dr) to calculate the hypothetical population size at [`tS`](../usage/configuration/config-reference.md#tS) given [`t0_pop_size`](../usage/configuration/config-reference.md#t0_pop0_size).

For example, configuring a larger setup birth rate than death rate would mean the population size at [`tS`](../usage/configuration/config-reference.md#tS) would be smaller to allow for it to grow and reach the size of [`t0_pop_size`](../usage/configuration/config-reference.md#t0_pop_size).

### Preliminary population initialisation

When then simulation begins at [`tS`], orphan children are spawned in to match the population size. At each timestep ([`simulation_time_step`](../usage/configuration/config-reference.md#simulation_time_step)), additional children will be spawned if the preliminary population is less than expected at that time. Notably, if the preliminary population is greater than expected (such as due to a setup higher death rate), Valipop will not remove individiuals and instead rely on the self correction of the distributions later on.

Once the initial spawned children are old enough to start partnering, they may start producing children also, meaning Valipop will need to spawn in fewer where necessary.

Valipop will stop spawning orphan children once it reaches the end of the preliminary population initialisation period. This period lasts for the greatest age $a$ specified in the [`birth/ordered_birth`](../usage/configuration/input-reference.md#birthordered_birth) (take min bound if max bound is unset). Essentially, when the first spawned orphan child reaches $a$ age, Valipop will rely on the population simulation alone to reach [`t0_pop_size`](../usage/configuration/config-reference.md#t0_pop_size).

### Preliminary population simulation

After the end of the preliminary population initialisation period, the population will be simulated regularly. Notably, the simulated population size at [`t0`](../usage/configuration/config-reference.md#t0) is usually not the same as [`t0_pop_size`](../usage/configuration/config-reference.md#t0_pop_size), and has a tendency to be greater to Valipop handling birth shortfalls. The population simulation will be describe in the Target Population section.

The only difference between the preliminary population and target population is when people were born. People born from [`tS`](../usage/configuration/config-reference.md#tS) until [`t0`](../usage/configuration/config-reference.md#t0) are part of the preliminary population. People born from [`t0`](../usage/configuration/config-reference.md#t0) until [`tE`](../usage/configuration/config-reference.md#tE) are part of the target population. After the end of the preliminary population initilisation, both populations are simulated in the same way, however only people from the target population are written to records. The preliminary population are not written to records (except maybe parents of target population people) to allow for a "burn-in" period where the population has developed past the initially spawned in orphan children.

## Target population

At each time step, Valipop determine the number of births and deaths occuring using the input distributions. It also handles migration and occupation changes based on input distributions.

### Births and Partnering

The Valipop simulation is birth-centered. It determines the number of births it needs to achieve in the time step for each age bracket of woman using the [`birth/ordered_birth](../usage/configuration/input-reference.md#birthordered_birth) distribution, and then identifies available mothers or single woman to birth the child. The number of children born from a pregnancy is determined by the [`birth/multiple_birth`](../usage/configuration/input-reference.md#birthmultiple_birth) distribution, and the sex is determined by the [`birth/ratio_birth`](../usage/configuration/input-reference.md#birthratio_birth) distribution.

Single woman selected are partnered with males based on the [`relationships/partenering`](../usage/configuration/input-reference.md#relationshipspartnering) distribution. Partenerships may result in a marriage or remain as a civil partnership based on the [`relationships/marriage`](../usage/configuration/input-reference.md#relationshipsmarriage) distribution.

Valipop will also simulate some births as adulterous using the [`birth/adulterous_birth`](../usage/configuration/input-reference#birthadulterous_birth.md#relationshipsseparation) distribution. Partnerships or marriages which did not produce new children may separate based on the [`relationships/separation`](../usage/configuration/input-reference.md#relationshipsseparation) distribution.

Whether a mother can have another child is determined by the configuration options [`min_birth_spacing`](../usage/configuration/config-reference.md#min_birth_spacing) and [`min_gestation_period`](../usage/configuration/config-reference.md#min_gestation_period).

There may be cases where there are not enough available woman or men to achieve the number of births. This is possible with small populations due to the inherent randomness of the simulation, however Valipop will try and correct for this in later calculations. How strictly Valipop corrects discrepancies is determined by the [`recovery_factor`](../usage/configuration/config-reference.md#recovery_factor) and [`proportional_recovery_factor`](../usage/configuration/config-reference.md#proportional_recovery_factor) options.

### Deaths

Valipop will determine the number of deaths to occur for each age at each time step using the [`death/males/lifetable`](../usage/configuration/input-reference.md#deathmaleslifetable) and [`death/females/lifetable`](../usage/configuration/input-reference.md#deathfemaleslifetable) distributions. Upon selecting the deaths, the cause of death is decided from the [`death/males/cause`](../usage/configuration/input-reference.md#deathmalescause) and [`death/females/cause`](../usage/configuration/input-reference.md#deathfemalescause) distributions.[`death/males/cause`](../usage/configuration/input-reference.md#deathmalescause).

### Annotations

Annotations refer to information attached to people which are not essential for the simulation. For example, the forename of a person will not influence how their family tree looks, but the birth rate could definetly influence it. The only essentially information is the births, deaths, and partnering, which could influence structure of the population, and this is also what is used to analyse the simulated population.

Annotations are added to people during the simulation to create a tangible identities for recorded individuals. Whilst Valipop utilisies unique ids to distinguish between people, linkage algorithms using Valipop records must identify people through the annotated information.

#### Naming

Newly born children are given forenames based on the [`annotations/male_forename`](../usage/configuration/input-reference.md#annotationsmale_forename) and [`annotations/female_forename`](../usage/configuration/input-reference.md#annotationsfemale_forename) distributions. If newly born children have parents, they will be given their parents surname. Newly born children without parents (by being spawned in), will be given a surname based on the [`annotations/surname`](../usage/configuration/input-reference.md#annotationssurname) distribution.

#### Occupation

Occupations of all people are controlled by the `OccupationChangeModel` class, which has two methods for handling occupations and changes. The first uses the [`annotations/occupation/change/male`](../usage/configuration/input-reference.md#annotationsoccupationchangemale) and [`annotations/occupation/change/female`](../usage/configuration/input-reference.md#annotationsoccupationchangefemale) distributions to determine the probability of an person changing to another type of job. The second resamples the [`annotations/occupation/male`](../usage/configuration/input-reference.md#annotationsoccupationmale) and [`annotations/occupation/female`](../usage/configuration/input-reference.md#annotationsoccupationfemale) distributions for every person every 10 years to simulate potentially changing jobs.

Currently, the `OccupationChangeModel` uses the latter model, however this can be changed by setting the `useChangeTables` property of `OccupationChangeModel` to `true` within the source code.

#### Geography

Valipop divides the geogrpahy of the population into `Areas`. An `Area` represents an area on the map wheres people can inhabit. Multiple abodes can exist in an `Area` and an `Area` usually represents a street, or a section of a street. A bounding box must be defined for the area in latitude and longitude and Valipop will determine how many abodes it can fit into the area. The array of all `Area`s, including the area address and bounding box are provided by the [`annotations/geography`](../usage/configuration/input-reference.md#annotationsgeography) file.