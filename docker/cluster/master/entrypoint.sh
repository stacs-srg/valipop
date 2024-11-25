#!/bin/bash

if [ -z "$1" ]; then
    HOST="localhost"
else
    HOST=$1
fi

if [ -z "$2" ]; then
    PORT=23177
else
    PORT=$2
fi

export SPARK_MASTER_OPTS="-Dspark.driver.bindAddress=0.0.0.0 -Dspark.driver.host=${HOST}"

../sbin/start-master.sh -p $PORT
