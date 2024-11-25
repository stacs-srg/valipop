#!/bin/bash

java \
    -cp valipop.jar \
    -Xmx24G \
    uk.ac.standrews.cs.valipop.implementations.CL_OBDModel \
    /app/src/main/resources/valipop/config/scot/config.txt


