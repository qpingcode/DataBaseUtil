#!/usr/bin/env bash
mvn install:install-file -DgroupId=com.intersys.jdbc -DartifactId=CacheDriver -Dversion=0.1 -Dpackaging=jar -Dfile=CacheDB.jar

