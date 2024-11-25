#!/bin/bash

FAILURES=$1

# Gets counts of appearance of stars
THREE=$(grep '\*\*\*$' $FAILURES | wc -l)
TWO=$(grep '\*\* $' $FAILURES | wc -l)
ONE=$(grep '\*  $' $FAILURES | wc -l)
DOTS=$(grep "\.  $" $FAILURES | wc -l)

ERRORS=$(grep "Error" $FAILURES | wc -l)

# This is me trying to divide by using bash...
T=3
DOT=$((DOTS / T))

# Calculates optomisation value
TOTAL=$((THREE * 4 + TWO * 3 + ONE * 2 + DOT))
#TOTAL=$((THREE * 3 + TWO * 2 + ONE))

if [ $ERRORS -eq 0 ]; then
  echo $TOTAL
else
  #echo -1
  echo $TOTAL
fi
