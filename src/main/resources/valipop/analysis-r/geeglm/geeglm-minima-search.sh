#!/bin/bash

PROJECT=$1
ANALYSIS=$2
FAILURES=$3

sh $PROJECT/src/main/resources/valipop/analysis-r/geeglm/failureSummary.sh $ANALYSIS $FAILURES
sh $PROJECT/src/main/resources/valipop/analysis-r/geeglm/starCounter.sh $FAILURES