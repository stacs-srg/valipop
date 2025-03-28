---
layout: default
title: ValiPop Simulation
markdown: kramdown
---

# ValiPop Population Simulation

## Preliminary population

### Preliminary population size

The initial size of preliminary population at [`tS`](../usage/configuration/config-reference.md#tS) is calculated by working backwards from [`t0_pop_size`](../usage/configuration/config-reference.md#t0_pop_size). ValiPop uses the setup birth rate [`set_up_br`](../usage/configuration/config-reference.md#set_up_br) and setup death rate [`set_up_dr`](../usage/configuration/config-reference.md#set_up_dr) to calculate the hypothetical population size at [`tS`](../usage/configuration/config-reference.md#tS) given [`t0_pop_size`](../usage/configuration/config-reference.md#t0_pop0_size).

For example, configuring a larger setup birth rate than death rate would mean the population size at [`tS`](../usage/configuration/config-reference.md#tS) would be smaller to allow for it to grow and reach the size of [`t0_pop_size`](../usage/configuration/config-reference.md#t0_pop_size).

### Preliminary population initialisation

When then simulation begins at [`tS`], orphan children are spawned in to match the population size. At each timestep ([`simulation_time_step`](../usage/configuration/config-reference.md#simulation_time_step)), additional children will be spawned if the preliminary population is less than expected at that time. Notably, if the preliminary population is greater than expected (such as due to a setup higher death rate), ValiPop will not remove individuals and instead rely on the self correction of the distributions later on.

Once the initial spawned children are old enough to start partnering, they may start producing children also, meaning ValiPop will need to spawn in fewer where necessary.

ValiPop will stop spawning orphan children once it reaches the end of the preliminary population initialisation period. This period lasts for the greatest age $a$ specified in the [`birth/ordered_birth`](../usage/configuration/input-reference.md#birthordered_birth) (take min bound if max bound is unset). Essentially, when the first spawned orphan child reaches $a$ age, ValiPop will rely on the population simulation alone to reach [`t0_pop_size`](../usage/configuration/config-reference.md#t0_pop_size).

### Preliminary population simulation

After the end of the preliminary population initialisation period, the population will be simulated regularly. Notably, the simulated population size at [`t0`](../usage/configuration/config-reference.md#t0) is usually not the same as [`t0_pop_size`](../usage/configuration/config-reference.md#t0_pop_size), and has a tendency to be greater to ValiPop handling birth shortfalls. The population simulation will be describe in the Target Population section.

The only difference between the preliminary population and target population is when people were born. People born from [`tS`](../usage/configuration/config-reference.md#tS) until [`t0`](../usage/configuration/config-reference.md#t0) are part of the preliminary population. People born from [`t0`](../usage/configuration/config-reference.md#t0) until [`tE`](../usage/configuration/config-reference.md#tE) are part of the target population. After the end of the preliminary population initilisation, both populations are simulated in the same way, however only people from the target population are written to records. The preliminary population are not written to records (except maybe parents of target population people) to allow for a "burn-in" period where the population has developed past the initially spawned in orphan children.

## Target population

At each time step, ValiPop determine the number of births and deaths occurring using the input distributions. It also handles migration and occupation changes based on input distributions.

### Births and Partnering

The ValiPop simulation is birth-centered. It determines the number of births it needs to achieve in the time step for each age bracket of woman using the [`birth/ordered_birth](../usage/configuration/input-reference.md#birthordered_birth) distribution, and then identifies available mothers or single woman to birth the child. The number of children born from a pregnancy is determined by the [`birth/multiple_birth`](../usage/configuration/input-reference.md#birthmultiple_birth) distribution, and the sex is determined by the [`birth/ratio_birth`](../usage/configuration/input-reference.md#birthratio_birth) distribution.

Single woman selected are partnered with males based on the [`relationships/partenering`](../usage/configuration/input-reference.md#relationshipspartnering) distribution. Partnerships may result in a marriage or remain as a civil partnership based on the [`relationships/marriage`](../usage/configuration/input-reference.md#relationshipsmarriage) distribution.

ValiPop will also simulate some births as adulterous using the [`birth/adulterous_birth`](../usage/configuration/input-reference#birthadulterous_birth.md#relationshipsseparation) distribution. Partnerships or marriages which did not produce new children may separate based on the [`relationships/separation`](../usage/configuration/input-reference.md#relationshipsseparation) distribution.

Whether a mother can have another child is determined by the configuration options [`min_birth_spacing`](../usage/configuration/config-reference.md#min_birth_spacing) and [`min_gestation_period`](../usage/configuration/config-reference.md#min_gestation_period).

There may be cases where there are not enough available woman or men to achieve the number of births. This is possible with small populations due to the inherent randomness of the simulation, however ValiPop will try and correct for this in later calculations. How strictly ValiPop corrects discrepancies is determined by the [`recovery_factor`](../usage/configuration/config-reference.md#recovery_factor) and [`proportional_recovery_factor`](../usage/configuration/config-reference.md#proportional_recovery_factor) options.

### Deaths

ValiPop will determine the number of deaths to occur for each age at each time step using the [`death/males/lifetable`](../usage/configuration/input-reference.md#deathmaleslifetable) and [`death/females/lifetable`](../usage/configuration/input-reference.md#deathfemaleslifetable) distributions. Upon selecting the deaths, the cause of death is decided from the [`death/males/cause`](../usage/configuration/input-reference.md#deathmalescause) and [`death/females/cause`](../usage/configuration/input-reference.md#deathfemalescause) distributions.[`death/males/cause`](../usage/configuration/input-reference.md#deathmalescause).

### Annotations

Annotations refer to information attached to people which are not essential for the simulation. For example, the forename of a person will not influence how their family tree looks, but the birth rate could definitely influence it. The only essentially information is the births, deaths, and partnering, which could influence structure of the population, and this is also what is used to analyse the simulated population.

Annotations are added to people during the simulation to create a tangible identities for recorded individuals. Whilst ValiPop utilises unique ids to distinguish between people, linkage algorithms using ValiPop records must identify people through the annotated information.

#### Naming

Newly born children are given forenames based on the [`annotations/male_forename`](../usage/configuration/input-reference.md#annotationsmale_forename) and [`annotations/female_forename`](../usage/configuration/input-reference.md#annotationsfemale_forename) distributions. If newly born children have parents, they will be given their parents surname. Newly born children without parents (by being spawned in), will be given a surname based on the [`annotations/surname`](../usage/configuration/input-reference.md#annotationssurname) distribution.

#### Occupation

Occupations of all people are controlled by the `OccupationChangeModel` class, which has two methods for handling occupations and changes. The first uses the [`annotations/occupation/change/male`](../usage/configuration/input-reference.md#annotationsoccupationchangemale) and [`annotations/occupation/change/female`](../usage/configuration/input-reference.md#annotationsoccupationchangefemale) distributions to determine the probability of an person changing to another type of job. The second resamples the [`annotations/occupation/male`](../usage/configuration/input-reference.md#annotationsoccupationmale) and [`annotations/occupation/female`](../usage/configuration/input-reference.md#annotationsoccupationfemale) distributions for every person every 10 years to simulate potentially changing jobs.

Currently, the `OccupationChangeModel` uses the latter model, however this can be changed by setting the `useChangeTables` property of `OccupationChangeModel` to `true` within the source code.

#### Geography

ValiPop divides the geography of the population into `Areas`. An `Area` represents an area on the map wheres people can inhabit. Multiple abodes can exist in an `Area` and an `Area` usually represents a street, or a section of a street. A bounding box must be defined for the area in latitude and longitude and ValiPop will determine how many abodes it can fit into the area. The array of all `Area`s, including the area address and bounding box are provided by the [`annotations/geography`](../usage/configuration/input-reference.md#annotationsgeography) file. Addresses are created from `Area`s and represent a single abode within the `Area`. Two separate families cannot share the same address.

After partnering, partners will move from to an address together, which is determined by the following logic:

- If both male and female do not have an address, they move to a random new address.

- If the male does not have an address, but the female does, they move to a new address at a random distance from the female address

- If the male has an address but the female does not, they move to a new address at a random distance from the male address

- If both male and female have an address, one of their addresses is picked and they move to a new address at at a random distance from that address.

If the partners of a partnership had children before hand, those children will move together to the new address, however parents and siblings will remain in the old address. Children born will inherit the address of their parents and will move with their parents where necessary until they find themselves a partner.

On the separation of a partnership, there is an equal chance for either partner to keep the children and keep the address. If one partner keeps the address, the other partner will move to a new random address. Whomever keeps the children have their children live at their new address.

### Migration

ValiPop always tries to balance migration to minimise the affect it could have on skewing the population statistics. All migration is handled by the `MigrationModel` class at each time step, which determines the number of people to migrate using the [`annotations/migration/rate`](../usage/configuration/input-reference.md#annotationsmigrationrate) distribution. Based on the number of people to migrate, the model will try to emigrate and immigrate an equal number of people to keep the population balanced.

Newly immigrated people are given random forenames based on the [`annotations/migration/male_forename`](../usage/configuration/input-reference.md#annotationsmigrationmale_forename) and [`annotations/migration/female_forename`](../usage/configuration/input-reference.md#annotationsmigrationfemale_forename) distributions and are given random surnames based on the [`annotations/migration/surname`](../usage/configuration/input-reference.md#annotationsmigrationsurname) distribution. The newly immigrated people are also designed to mimic the properties of emigrated people or families to preserve the structure of the population.