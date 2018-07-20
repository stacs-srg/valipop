#!/bin/bash

# Expected arguments
ANALYSIS=$1
FAILURES=$2

# Makes temp files
DATE=$(date +%s)

DIRPATH="$(echo ~)/temp/$DATE/$RANDOM"
mkdir -p $DIRPATH

FILEPATH1="$DIRPATH/temp.txt"
FILEPATH2="$DIRPATH/temp2.txt"
touch $FILEPATH1
touch $FILEPATH2

#touch ~/temp.txt
#touch ~/temp2.txt

# Gets titles
grep -n "GEEGLM" $ANALYSIS > $FAILURES

# Gets STATS interactions that are significant
grep -n "STAT" $ANALYSIS > $FILEPATH1
grep " \." $FILEPATH1 >> $FAILURES
grep "\*" $FILEPATH1 >> $FAILURES

# Gets any error messages
grep -n "Error" $ANALYSIS >> $FAILURES

# Sorts by line number from origonal ANALYSIS file
sort -n -t: -k1 $FAILURES > $FILEPATH1

# Strips HTML tags if present
sed -e 's/<[^>]*>//g' $FILEPATH1 > $FILEPATH2

# Strips line numbers
sed 's/ *[0-9]*.//' $FILEPATH1 > $FAILURES

# Removes temp files
rm -rf $DIRPATH