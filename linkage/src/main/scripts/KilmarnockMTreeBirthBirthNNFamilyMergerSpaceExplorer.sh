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


mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 3 5 3" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer353.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 7 5 3" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer753.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 9 5 3" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer953.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 16 5 3" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer1653.txt


mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 3 10 3" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer3103.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 7 10 3" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer7103.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 9 10 3" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer9103.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 16 10 3" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer16103.txt


mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 3 20 3" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer3203.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 7 20 3" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer7203.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 9 20 3" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer9203.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 16 20 3" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer16203.txt


mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 3 5 7" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer357.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 7 5 7" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer757.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 7 5 7" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer757.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 16 5 7" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer1657.txt


mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 3 10 7" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer3107.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 7 10 7" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer7107.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 7 10 7" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer7107.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 16 10 7" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer16107.txt


mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 3 20 7" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer3207.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 7 20 7" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer7207.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 7 20 7" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer7207.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 16 20 7" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer16207.txt


mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 3 5 9" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer359.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 7 5 9" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer759.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 9 5 9" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer959.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 16 5 9" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer1659.txt


mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 3 10 9" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer3109.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 7 10 9" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer7109.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 9 10 9" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer9109.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 16 10 9" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer16109.txt


mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 3 20 9" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer3209.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 7 20 9" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer7209.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 9 20 9" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer9209.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 16 20 9" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer16209.txt


mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 3 5 16" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer3516.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 7 5 16" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer7516.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 9 5 16" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer9516.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 16 5 16" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer16516.txt


mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 3 10 16" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer31016.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 7 10 16" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer71016.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 9 10 16" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer91016.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 16 10 16" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer161016.txt


mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 3 20 16" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer32016.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 7 20 16" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer72016.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 9 20 16" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer92016.txt

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.resolve.KilmarnockMTreeBirthBirthNNFamilyMergerTruthChecker" -e -Dexec.args="$births $deaths $marriages 16 20 16" > /tmp/KilmarnockMTreeBirthBirthNNFamilyMergerSpaceExplorer162016.txt
