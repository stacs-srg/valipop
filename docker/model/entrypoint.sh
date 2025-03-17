#!/bin/bash

#java \
    #-cp valipop.jar \
    #-Xmx30G \
    #uk.ac.standrews.cs.valipop.implementations.CL_OBDModel \
    #/app/src/main/resources/valipop/config/scot/config.txt

#if [ -z $1 ]; then
    #MASTER_URL="local[*]"
#else
    #MASTER_URL=$1
#fi

#if [ -z $2 ]; then
    #HOST="localhost"
#else
    #HOST=$2
#fi

java \
    -jar valipop.jar \
    $1

#spark/bin/spark-submit \
    #--class uk.ac.standrews.cs.valipop.implementations.CL_OBDModel \
    #--master "$MASTER_URL" \
    #--driver-memory 30G \
    #--executor-memory 30G \
    #--executor-cores 1 \
    #--total-executor-cores 36 \
    #--conf spark.driver.host=$HOST \
    #--conf spark.driver.bindAddress=0.0.0.0 \
    #--conf spark.driver.port=5055 \
    #valipop.jar \
    #/app/src/main/resources/valipop/config/scot/config.txt
