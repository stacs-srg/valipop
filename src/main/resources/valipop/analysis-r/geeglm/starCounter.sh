#!/bin/bash

FAILURES=$1

# Gets counts of appearance of stars
THREE=$(fgrep -o \*\*\* $FAILURES | wc -l)
TWO=$(fgrep -o \*\* $FAILURES | wc -l)
ONE=$(fgrep -o \* $FAILURES | wc -l)
DOTS=$(fgrep -o " ." $FAILURES | wc -c)

ERRORS=$(fgrep -o " error" $FAILURES | wc -c)

# This is me trying to divide by using bash...
T=3
DOT=`expr $DOTS / $T`

# Corrects for counting of substrings
TWO=`expr $TWO - $THREE`
ONE=`expr $ONE - $THREE \* 3 - $TWO \* 2`

# Calculates optomisation value
# TOTAL=`expr $THREE \* 4 + $TWO \* 3 + $ONE \* 2 + $DOT`
TOTAL=`expr $THREE \* 3 + $TWO \* 2 + $ONE`

if [ "$ERRORS" -eq "0" ]; then
  echo $TOTAL
else
  echo -1
fi
