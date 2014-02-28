#!/bin/bash

# we want to run the jinvestor downloader.

#if [ ! -f "etc/aws.properties" ]; then
	#	echo "The etc directory must have your aws.properties file in it."
#fi

if [ ! -d "logs" ]; then
	mkdir logs
fi

JAVA_OPTS="-Xmx1024m -Xms256m -Dspring.profiles.active=localHBase -Dlog4j.configuration=file:etc/log4j.xml"

# discover the jar
JAR=$(ls lib | grep "jinvestor.jhouse.download")

java $JAVA_OPTS -jar lib/$JAR -d &> logs/stdout.log &