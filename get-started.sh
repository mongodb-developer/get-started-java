#!/bin/bash

MONGODB_URI=${1}
if [ -z ${MONGODB_URI} ]
then
    read -p "MONGODB URI (Required): " MONGODB_URI
fi 

DRIVER_VERSION=${2:-4.1.1}
echo "Executing ... "
docker run --rm -e MONGODB_URI=${MONGODB_URI} \
    -v "$(pwd)":/workspace \
    -w /workspace/java start-java \
    "sed -i s#mongodb-driver-sync\<\/artifactId\>\<version\>[x0-9]\.[x0-9].[x0-9]#mongodb-driver-sync\<\/artifactId\>\<version\>${DRIVER_VERSION}#g \
    /workspace/java/pom.xml; \
    mvn -Dmaven.repo.local=/workspace/.m2/repository package; \
    java -cp ./target/start-1.0-SNAPSHOT.jar com.start.Getstarted"
