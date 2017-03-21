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

FIXED_ARGS="$births $deaths $marriages"

DISTANCES=(3 7 9 16)
FAMILY_SIZE=(5 10 20)
MERGE_DISTANCES=(3 7 9 16)

parallel --no-notice -j4 --joblog /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer-joblog.txt \
    "mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass='uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker' -e -Dexec.args='$FIXED_ARGS {1} {2} {3}' > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer-{1}-{2}-{3}.txt" \
    ::: ${DISTANCES[@]} \
    ::: ${FAMILY_SIZE[@]} \
    ::: ${MERGE_DISTANCES[@]}

