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

                # RUN 4 jobs in parallel at a time
				CMD -e -Dexec.args="$FIXED_ARGS $distance $size 3" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer${distance}${size}3.txt &
				P1=$!
				CMD -e -Dexec.args="$FIXED_ARGS $distance $size 7" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer${distance}${size}7.txt &
				P2=$!
				CMD -e -Dexec.args="$FIXED_ARGS $distance $size 9" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer${distance}${size}9.txt &
				P3=$!
				CMD -e -Dexec.args="$FIXED_ARGS $distance $size 16" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer${distance}${size}16.txt &
				P4=$!
				wait $P1 $P2 $P3 $P4
	done
done
