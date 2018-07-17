#!/bin/bash

DATE=$(date +%s)

DIRPATH="$(echo ~)/temp/$DATE"
mkdir -p $DIRPATH

FILEPATH="$DIRPATH/test.txt"
touch $FILEPATH

rm -rf $DIRPATH