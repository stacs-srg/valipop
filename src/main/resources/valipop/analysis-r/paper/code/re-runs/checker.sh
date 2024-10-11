#!/bin/bash

# $1 - path to analysis life to check
# $2 - run start time

OUT=$(cat $1 | grep -c $2)

if [[ $OUT == "0" ]]
then
	echo "FAILED"
else
	echo "CORRECT"
fi
