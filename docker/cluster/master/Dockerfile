FROM spark:4.1.2-scala2.13-java21-ubuntu

EXPOSE 8080

ARG HOST_NAME localhost

ENV SPARK_NO_DAEMONIZE=true

RUN ls

COPY docker/cluster/master/entrypoint.sh .

ENTRYPOINT [ "./entrypoint.sh" ]
