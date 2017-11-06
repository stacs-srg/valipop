---
layout: default
title: ValiPop
markdown: kramdown
---

## _ValiPop_

_ValiPop_ is a micro-simulation model for generating synthetic genealogical populations, 
taking as input a set of desired summary statistics. _ValiPop_ also verifies that the 
desired properties exist in the generated populations. _ValiPop_ is highly scalable and 
customisable, it is able to create populations for a wide range of purposes.  The focus 
of our research is the use of many synthetic genealogical populations to evaluate and 
improve data linkage algorithms.

_ValiPop_'s micro-simulation model is written in Java. The supporting verification analysis 
and statistical code is written in R.

## Working with _ValiPop_

_ValiPop_ as standard is currently able to create populations in the form of the vital event 
records for Scotland ([NRS](https://www.nrscotland.gov.uk/research/guides/birth-death-and-marriage-records/statutory-registers-of-births-deaths-and-marriages)). 
Guidance on how to define other forms of event records can be found [here](guides/other-record-types.md).

Some summary statistics are also packaged with _ValiPop_, details of these and how to use your 
own statistics can be found [here](guides/summary-statistics.md).

Depending on the desired behaviour of _ValiPop_ some special implementations can be found 
[here](guides/implementations.md) and a guide to the _ValiPop_ configuration file can be found [here](guides/config.md).  

For more advanced requirements it is possible to extend the code base to meet your 
needs. Helpful resources for working with _ValiPop_ include an [overview](guides/overview.md) of the simulation, 
[JavaDoc for _ValiPop_](https://quicksilver.host.cs.st-andrews.ac.uk/apidocs/population-model/), 
the [code base](https://github.com/stacs-srg/population-model), and the below publications.

For further information, queries or collaborations please contact Tom Dalton (tsd4@st-andrews.ac.uk)

## Publications

...


