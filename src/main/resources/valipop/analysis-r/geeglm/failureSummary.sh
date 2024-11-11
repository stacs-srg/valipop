#!/bin/bash

# Expected arguments
ANALYSIS=$1
FAILURES=$2

# Gets titles and strip tags
grep -n "GEEGLM" $ANALYSIS | sed -e 's/<[^>]*>//g' > $FAILURES

# Gets STATS interactions that are significant
#  (that match '.  ', '*  ', '** ', or '***')
grep -n "STAT" $ANALYSIS | grep "[\*.][* ][* ]$" >> $FAILURES

# Gets any error messages and strip tags
grep -n "Error" $ANALYSIS | sed -e 's/<[^>]*>//g' >> $FAILURES

# Sorts by line number from original ANALYSIS file
sort -n -t: -k1 -o $FAILURES $FAILURES

# Strips line numbers
sed -i 's/ *[0-9]*.//' $FAILURES