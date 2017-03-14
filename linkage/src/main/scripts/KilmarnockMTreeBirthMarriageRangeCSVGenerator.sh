#!/bin/sh
#
# Copyright 2016 Digitising Scotland project:
# <http://digitisingscotland.cs.st-andrews.ac.uk/>
#
#

BASEDIR=$(dirname "$0")
cd $BASEDIR/../../..

births="/DigitisingScotland/KilmarnockBDM/births_post71.csv"
deaths="/DigitisingScotland/KilmarnockBDM/deaths.csv"
marriages="/DigitisingScotland/KilmarnockBDM/marriages_pre92.csv"

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthWithinDistanceBFTGenerator" -e -Dexec.args="$births $deaths $marriages"