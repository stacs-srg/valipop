#!/bin/bash

ANALYSIS=$1
FAILURES=$2

sh src/main/resources/valipop/analysis-r/geeglm/failureSummary.sh $ANALYSIS $FAILURES
sh src/main/resources/valipop/analysis-r/geeglm/starCounter.sh $FAILURES