#!/bin/bash

# Expected arguments
ANALYSIS=$1
FAILURES=$2

# Makes temp files
touch ~/temp.txt
touch ~/temp2.txt

# Gets titles
grep -n "GEEGLM" $ANALYSIS > $FAILURES

# Gets STATS interactions that are significant
grep -n "STAT" $ANALYSIS > ~/temp.txt
grep " \." ~/temp.txt >> $FAILURES
grep "\*" ~/temp.txt >> $FAILURES

# Gets any error messages
grep -n "Error" $ANALYSIS >> $FAILURES

# Sorts by line number from origonal ANALYSIS file
sort -n -t: -k1 $FAILURES > ~/temp.txt

# Strips HTML tags if present
sed -e 's/<[^>]*>//g' ~/temp.txt > ~/temp2.txt

# Strips line numbers
sed 's/ *[0-9]*.//' ~/temp2.txt > $FAILURES

# Removes temp files
rm -rf ~/temp.txt
rm -rf ~/temp2.txt