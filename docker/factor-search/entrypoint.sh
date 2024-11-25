#!/bin/sh

if [ -z $1 ]; then
    MASTER_URL="local[*]"
else
    MASTER_URL=$1
fi

if [ -z $2 ]; then
    HOST="localhost"
else
    HOST=$2
fi

spark/bin/spark-submit \
    --class uk.ac.standrews.cs.valipop.implementations.DistributedFactorSearch \
    --master "$MASTER_URL" \
    --driver-memory 24G \
    --executor-memory 24G \
    --executor-cores 3 \
    --total-executor-cores 36 \
    --conf spark.driver.host=$HOST \
    --conf spark.driver.bindAddress=0.0.0.0 \
    --conf spark.driver.port=5055 \
    valipop.jar \
    /app/src/main/resources/valipop/inputs/synthetic-scotland/ \
    10000 \
    distributed \
    1 \
    "0,0.5,1" \
    "0,0.5,1" \
    /app/results \
    /app/results \
    1E-66 \
    /app
