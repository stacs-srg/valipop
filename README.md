# ValiPop

_ValiPop_ is a micro-simulation model for generating synthetic genealogical populations
from a set of desired statistics. _ValiPop_ also verifies that the 
desired properties exist in the generated populations. _ValiPop_ is highly scalable and 
customisable, it is able to create populations for a wide range of purposes.  The focus 
of our research is the use of many synthetic genealogical populations to evaluate and 
improve data linkage algorithms.

_ValiPop_'s micro-simulation model is written in Java. The supporting verification analysis 
and statistical code is written in R.

## Running ValiPop

### Via Java

To run ValiPop with its JAR file, you will need to have installed:

- [R 4.4.2 or higher](https://cran.r-project.org/)
- Java 21 or higher

Then you can follow these steps to run the project

```shell
# Install the geepack R package on your system
R -e "install.packages('geepack', repos = c(CRAN = 'https://cloud.r-project.org'))"

# Run the JAR with java
java -jar valipop.jar <valipop-args>
```

[Learn more about running with Java.](https://stacs-srg.github.io/population-model/usage/execution/java.html)


### Via Docker

To run ValiPop as a docker container, you will need to have [docker](https://www.docker.com/) installed.

Then you can follow these steps to run the project

```shell
# Pull the container
docker pull ghcr.io/stacs-srg/valipop:master

# Run the container
docker run ghcr.io/stacs-srg/valipop:master <valipop-args>
```

[Learn more about running with Docker.](https://stacs-srg.github.io/population-model/usage/execution/docker.html)


## Building ValiPop from source

To build ValiPop, you will need to have installed:

- [Git](https://git-scm.com/)
- Java 21 or higher
- [Maven](https://maven.apache.org/)

Then you can follow these steps to build the project:

```shell
# Open a terminal

# Clone the repository
git clone https://github.com/stacs-srg/population-model

# Navigate to the project repository
cd valipop

# Installing dependencies, compiling, and packaging into JARs
mvn clean package -Dmaven.test.skip -Dmaven.repo.local=repository

# The build should be created in `target/`, including the runnable JARs
```