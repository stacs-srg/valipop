#!/bin/sh

MYSELF=`which "$0" 2>/dev/null`
[ $? -gt 0 -a -f "$0" ] && MYSELF="./$0"
java=java

CLASSLI_JVM_ARGS="${CLASSLI_JVM_ARGS:--Xmx1g}"

if test -n "$JAVA_HOME"; then
    java="$JAVA_HOME/bin/java"
fi
exec "$java" $CLASSLI_JVM_ARGS -jar $MYSELF "$@"
exit 1
