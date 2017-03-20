#!/bin/sh
#
# Copyright 2016 Digitising Scotland project:
# <http://digitisingscotland.cs.st-andrews.ac.uk/>
#
#

BASEDIR=$(dirname "$0")
cd $BASEDIR/../../..


births="/DigitisingScotland/KilmarnockBDM/births.csv"
deaths="/DigitisingScotland/KilmarnockBDM/deaths.csv"
marriages="/DigitisingScotland/KilmarnockBDM/marriages.csv"

alias CMD='mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" '
FIXED_ARGS="$births $deaths $marriages"

DISTANCES=(3 7 9 16)
FAMILY_SIZE=(5 10 20)
MERGE_DISTANCES=(3 7 9 16)

for distance in ${DISTANCES[@]}; do
    for size in ${FAMILY_SIZE[@]}; do
        for merge_distance in ${MERGE_DISTANCES[@]}; do
            CMD -e -Dexec.args="$FIXED_ARGS $distance $size $merge_distance" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer${distance}${size}${merge_distance}.txt
        done
    done
done
