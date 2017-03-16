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
distance="8"
size="15"
merge_distance="8"

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyCentroidMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages $distance $size $merge_distance"