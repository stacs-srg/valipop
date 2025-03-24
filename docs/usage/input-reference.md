---
layout: default
title: ValiPop Input Distribution Reference
markdown: kramdown
---

# Valipop Input Distribution Reference

## Input Distribution Format

Each individual distribution file contains at least the following meta information at the start of the file.

```
YEAR	<year>
POPULATION	<location>
SOURCE	<source>
LABELS	<tab-separated column labels>
DATA
...
```

Each meta field and value must be separated by a tab character. You may include your own fields, but only the following are read by Valipop:

- `YEAR` specifies what year the distribution applies to. 
- `POPULATION` specifies what population the distribution is based on.
- `SOURCE` specifies where the distribution was acquired.
- `LABELS` specifies the labels of each column in the data, separated by tab characters.

Everything after the `DATA` field will be recorded as the input distribution data.

The format of the data depends on what the distribution is for, but all formats use tab characters to separate values.

Notably, ranges can be used to represent several positive integer values within the data. Ranges can either be of the form `a-b` representing the values between positive integers `a` and `b` inclusive, or as `c+` representing the values of `c` and greater.

### Single Input Data

The data is separated into a 'year' and 'value' column. Each row specifies the value for the given year. For any time, Valipop will use the value of the nearest given year. The `YEAR` meta field is ignored by Valipop here as the data represents the values across multiple years.

The following shows an input distribution for the property [`birth/ratio_birth`](#birthratio_birth) (proportion of births born male), which uses single input data.

```
...
DATA
1600	0.5
1650	0.56
1700	0.51
1801	0.48
1892	0.47
```

### Name Data

The data is separated into a 'name' and 'probability' column. Each row specifies the probability for a name. The sum of the probabilites should sum to one.

The following shows the name data for the property [`annotations/female_forename/`](#annotationsfemale_forename) (probability of female forenames).

```
...
DATA
Aaisha	3.37840120541355e-05
Aaishah	2.02704072324813e-05
Aalia	3.04056108487219e-05
Aaliya	3.7162413259549e-05
Aaliyah	0.000375002533801
Aamena	2.02704072324813e-05
Aamenah	1.68920060270678e-05
Aamina	4.72976168757897e-05
...
```

### 2D Age-Dependent Enumerated Data

The data is a 2D table with age or age ranges in the first column, and probabilities in the remaining columns. Each remaining column represents an enumerated value, and each row represents the probability distribution of the enumerated values at an age or age range. The sum of the probabilities on each row should sum to one. The `LABELS` meta field should specify the enumerated value of each column (skipping the age column)

The following shows the 2D age-dependent enumerated data for the property [`annotations/occupation/male/`](#annotationsoccupationmale) (probability of male occupations at a given age). The first labelled column ' ' represents unemployment in this case.

```
...
LABELS	 	Farmer	Teacher	Chimney Sweeper
DATA
0-10	1.0	0.0	0.0	0.0
11-16	0.82	0.0	0.0	0.18
17-18	0.61	0.34	0.0	0.05
19	0.41	0.38	0.2	0.01
20-31	0.14	0.5	0.36	0
32+	0.16	0.52	0.32	0
```

### 2D Doubly Enumerated Data

The data is a 2D table with both row and columns representing enumerated values. The first column specifies the enumerated value for each rows and the `LABELS` meta field specifies the enumerated value of each column. The values represent probabilites and each row should sum to one.

The following shows 2D enumerated data for the property [`annotations/occupation/change/male/`](#annotationsoccupationchangemale) (proportion of occupations males change to for each current occupation).

```
...
LABELS	 	Farmer	Teacher	Chimney Sweeper
DATA
 	0.7	0.25	0.05	0.0
Farmer	0.15	0.8	0.05	0.0
Teacher	0.2	0.2	0.6	0.0
Chimney Sweeper	0.6	0.3	0.0	0.1
```

### 1D Age-Dependent Data

The data is separated into 'age or age range' and 'value' columns. Each row specifies a value for a given age or age range. 

The following shows 1D age-dependent data for the property [`death/males/lifetable`](#deathmaleslifetable) (probability of death of at a given age or age range).

```
...
DATA
0-4	0.06089
5-10	0.00821
11-14	0.00483
15-19	0.00724
20-29	0.00916
30-39	0.01058
40-49	0.01443
50-59	0.02170
60-69	0.04430
70-79	0.09948
80-89	0.20741
90-99	0.36215
100+	0.28125
```

### 2D Age-Dependent Data

The data is a 2D table with age or age ranges for each row, and some numerical value or value range for each column. The first column specifies the age or age range for each row, and the `LABELS` meta field specifies the value or value range for each column.

The following shows 2D double age-depedent data for the property [`birth/ordered_birth/`](#birthordered_birth) (probabilites of having some number of children for each age).

```
LABELS	0	1	2	3	4	5+
DATA
0-14	0	0	0	0	0	0
15-19	0.0622909	0.010868	0.0015411	0	0	0
20-24	0.065174	0.033412	0.018144	0.004536	0.001134	0.0001134
25-29	0.03808	0.03872	0.044088	0.016032	0.00668	0.000668
30-34	0.011442424	0.025012121	0.030751515	0.012872727	0.005721212	0.0005721212
35-39	0.0022	0.00658	0.00946	0.00462	0.00264	0.000264
40-49	0.000264	0.000662	0.000864	0.000528	0.000432	0.0000432
50-54	0	0	0	0	0	0
55+	0	0	0	0	0	0
```

## Directory Structure

The structure of the input distributions directory is shown in the following tree:

```
my-input-distribution/
│
├───annotations/
│   ├───female_forename/
│   ├───male_forename/
│   ├───surname/
│   ├───geography/
│   │
│   ├───occupation/
│   │   ├───change/
|   │   │   ├───female/
|   │   │   └───male/
|   |   |
│   │   ├───female/
│   │   └───male/
|   |
│   └───migration/
│       ├───female_forename/
│       ├───male_forename/
│       ├───surname/
│       └───rate/
|       
├───birth/
│   ├───adulterous_birth/
│   ├───multiple_birth/
│   ├───ordered_birth/
│   └───ratio_birth/
│
├───death/
│   ├───females/
│   │   ├───cause/
│   │   └───lifetable/
│   │
│   └───males/
│       ├───cause/
│       └───lifetable/
│
└───relationships/
    ├───marriage/
    ├───partnering/
    └───separation/
```

### Properties

<dl>

<dt>
<a name="annotationsfemale_forename">
<code>annotations/female_forename</code>
</a>
</dt>

<dd markdown="1">
The probability of each name a newborn female could be given. Uses the [Name Data](#name-data) format.
</dd>

<dt>
<a name="annotationsmale_forename">
<code>annotations/male_forename</code>
</a>
</dt>

<dd markdown="1">
The probability of each name a newborn male could be given. Uses the [Name Data](#name-data) format.
</dd>

<dt>
<a name="annotationssurname">
<code>annotations/surname</code>
</a>
</dt>

<dd markdown="1">
The probability of each surname a newly spawned family could be given. Uses the [Name Data](#name-data) format.
</dd>

<dt>
<a name="annotationsgeography">
<code>annotations/geogrpahy</code>
</a>
</dt>

<dd markdown="1">
The geography the population is set in. This requires a single JSON file which defines the array of Areas a person can inhabit. An Area is defined by the following minimal JSON:

```json
{
    "place_id": <OSM place id>,
    "road": <road>,
    "suburb" <suburb>,
    "town": <town>,
    "county": <county>,
    "state": <country>,
    "postcode": <postcode>,
    "boundingbox": [<min lat>, <max lat>, <min long>, <max long>],
}
```
</dd>

<dt>
<a name="annotationsoccupationchangefemale">
<code>annotations/occupation/change/female</code>
</a>
</dt>

<dd markdown="1">
The proportion of occupations a female will change to from their current occupation. Uses the [2D double enumerated data](#2d-doubly-enumerated-data) format.
</dd>

<dt>
<a name="annotationsoccupationchangemale">
<code>annotations/occupation/change/male</code>
</a>
</dt>

<dd markdown="1">
The proportion of occupations a male will change to from their current occupation. Uses the [2D double enumerated data](#2d-doubly-enumerated-data) format.
</dd>

<dt>
<a name="annotationsoccupationfemale">
<code>annotations/occupation/female</code>
</a>
</dt>

<dd markdown="1">
The probabilities of occupations for a male at a given. Uses the [2D age-dependent enumerated data](#2d-age-dependent-enumerated-data) format.
</dd>

<dt>
<a name="annotationsoccupationmale">
<code>annotations/occupation/male</code>
</a>
</dt>

<dd markdown="1">
The probabilities of occupations for a male at a given. Uses the [2D age-dependent enumerated data](#2d-age-dependent-enumerated-data) format.
</dd>

<dt>
<a name="annotationsmigrationfemale_forename">
<code>annotations/migration/female_forename</code>
</a>
</dt>

<dd markdown="1">
The probability of each forename a newly immigrated female could have. Uses the [Name Data](#name-data) format.
</dd>

<dt>
<a name="annotationsmigrationmale_forename">
<code>annotations/migration/male_forename</code>
</a>
</dt>

<dd markdown="1">
The probability of each forename a newly immigrated male could have. Uses the [Name Data](#name-data) format.
</dd>

<dt>
<a name="annotationsmigrationsurname">
<code>annotations/migration/surname</code>
</a>
</dt>

<dd markdown="1">
The probability of each surname a newly immigrated person could have. Uses the [Name Data](#name-data) format.
</dd>

<dt>
<a name="birthadulterous_birth">
<code>birth/adulterous_birth</code>
</a>
</dt>

<dd markdown="1">
The proportion of illegitimate births among all births. Uses the [1D age-depedent data](#1d-age-dependent-data) format.
</dd>

<dt>
<a name="birthmultiple_birth">
<code>birth/multiple_birth</code>
</a>
</dt>

<dd markdown="1">
The proportion of maternaties producing a given number of childrens. For example, whether a pregnancy results in twins, triplet, or just a single child. Uses the [2D age-dependent data](#2d-age-dependent-data) format, with the number of children produced on the columns. Each row should sum to one (or zero if no births allowed).
</dd>

<dt>
<a name="birthordered_birth">
<code>birth/ordered_birth</code>
</a>
</dt>

<dd markdown="1">
The probability of a mother having a given number of children at a given age. Uses the [2d age-dependent data](#2d-age-dependent-data) format.
</dd>

<dt>
<a name="birthratio_birth">
<code>birth/ratio_birth</code>
</a>
</dt>

<dd markdown="1">
The proportion of children born male. Uses the [single input data](#single-input-data) format.
</dd>

<dt>
<a name="deathfemalescause">
<code>death/females/cause</code>
</a>
</dt>

<dd markdown="1">
The proportions of causes of death for female deaths at a given age. Uses the HICOD notation to enumerate causes of deaths. Uses the [2d-age-dependent-enumerated data](#2d-age-dependent-enumerated-data) format.
</dd>

<dt>
<a name="deathfemaleslifetable">
<code>death/females/lifetable</code>
</a>
</dt>

<dd markdown="1">
The probability of a female dying at a given age. Uses the [1d age-dependent data](#1d-age-dependent-data) format.
</dd>

<dt>
<a name="deathmalescause">
<code>death/males/cause</code>
</a>
</dt>

<dd markdown="1">
The proportions of causes of death for male deaths at a given age. Uses the HICOD notation to enumerate causes of deaths for each column. Uses the [2d-age-dependent-enumerated data](#2d-age-dependent-enumerated-data) format.
</dd>

<dt>
<a name="deathmaleslifetable">
<code>death/males/lifetable</code>
</a>
</dt>

<dd markdown="1">
The probability of a male dying at a given age. Uses the [1d age-dependent data](#1d-age-dependent-data) format.
</dd>

<dt>
<a name="relationshipsmarriage">
<code>relationships/marriage</code>
</a>
</dt>

<dd markdown="1">
The proportion of children born within a marriage, as opposed to a civil partnership. Uses the [1d age-dependent data](#1d-age-dependent-data) format.
</dd>

<dt>
<a name="relationshipspartnering">
<code>relationships/partnering</code>
</a>
</dt>

<dd markdown="1">
The proportion of male ages females of a given age will partner with. Female ages for each row, male ages for each column. Uses the [2d age-dependent data](#2d-age-dependent-data) format.
</dd>

<dt>
<a name="relationshipsseparation">
<code>relationships/separation</code>
</a>
</dt>

<dd markdown="1">
Out of the total number of marriages with children, how many divorce in a given year for each number of children in the marriage. Uses the [2d-age-dependent-data](#2d-age-dependent-data) format.
</dd>

</dl>

## Input Distribution files

Each end directory (directory without sub directories) represents a property of the population. Within an end directory is any number of input distribution files for that property, often for different years. The files can have any name, but must be located in the correct end directory.

In the following example, the marriage property (which defines the proportion of parents that are married), contains three input distributions from different years. (The different years must be specified in the `YEAR` meta field for Valipop to understand).

```
└───relationships/
    └───marriage/
        ├───marriage_1938.txt
        ├───marriage_1953.txt
        └───marriage_1973.txt
```

Each input distribution of a property will apply for a period of time during the simulation. The length of the period is defined by the `input_width` option in the config file. Valipop will divide the given input distributions into these equal periods based on which input distribution is closest to the end time of that period.

For example, using the input distributions defined above in a simulation running from years 1900 to 2000 with an input width of 10 years. The input distributions will be divided over the following periods.

```
                       marriage_1953.txt
                         ┌─────┴─────┐
     marriage_1938.txt                   marriage_1973.txt
 ┌───────────┴───────────┐           ┌───────────┴───────────┐

1900  1910  1920  1930  1940  1950  1960  1970  1980  1990  2000
 ├─────┼─────┼─────┼─────┼─────┼─────┼─────┼─────┼─────┼─────┤
```
