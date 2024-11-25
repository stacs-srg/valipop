#!/bin/bash
if [ -z $1 ]; then
    MASTER_URL="localhost"
else
    MASTER_URL=$1
fi

if [ -z $2 ]; then
    HOST="localhost"
else
    HOST=$2
fi

export SPARK_WORKER_OPTS="-Dspark.driver.bindAddress=0.0.0.0 -Dspark.driver.host=${HOST}"

spark/sbin/start-worker.sh -d "/app" "$MASTER_URL" 