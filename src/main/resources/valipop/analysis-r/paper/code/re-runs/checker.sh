#!/bin/bash

OUT=$(cat /cs/tmp/tsd4/results/$1/$2/analysis.html | grep -c $2)

if [[ $OUT == "0" ]]
then
	echo "FAILED"
else
	echo "CORRECT"
fi
