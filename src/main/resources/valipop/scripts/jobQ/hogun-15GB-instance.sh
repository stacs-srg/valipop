#!/bin/bash

cd population-model
nohup sh src/main/resources/valipop/scripts/start-job-q-instance.sh src/main/resources/valipop/scripts/jobQ/status-hogun.txt src/main/resources/valipop/scripts/jobQ/job-q-clusters.csv 15 1 0.5 > job-run-"$1"-`hostname`.txt
