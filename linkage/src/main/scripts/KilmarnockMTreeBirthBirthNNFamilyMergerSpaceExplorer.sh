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

MVN_ARGS="-q -Dexec.cleanupDaemonThreads=false"
MVN_ARGS="$MVN_ARGS -Dexec.mainClass='uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker' -e"

DISTANCES=(3 7 9 16)
FAMILY_SIZE=(5 10 20)
MERGE_DISTANCES=(3 7 9 16)

# become root
# mkdir /data/digitising_scotland_outputs
# chown -R secure:secure /data/digitising_scotland_outputs

# do the same with /data/digitising_scotland_tmp
# and set the tmpdir to this directory. otherwise we risk running out of disk space with /tmp
export _JAVA_OPTIONS=-Djava.io.tmpdir=/data/digitising_scotland_tmp

parallel --no-notice -j4 --joblog /data/digitising_scotland_outputs/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer-joblog.txt \
    --eta \
    "mvn exec:java $MVN_ARGS -Dexec.args='$FIXED_ARGS {1} {2} {3}' > /data/digitising_scotland_outputs/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer-{1}-{2}-{3}.txt" \
    ::: ${DISTANCES[@]} \
    ::: ${FAMILY_SIZE[@]} \
    ::: ${MERGE_DISTANCES[@]}

