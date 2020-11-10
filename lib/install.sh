#!/usr/bin/env bash
mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc8 -Dversion=0.1 -Dpackaging=jar -Dfile=ojdbc8.jar


mvn install:install-file -DgroupId=com.intersys.jdbc -DartifactId=CacheDriver -Dversion=0.1 -Dpackaging=jar -Dfile=CacheDB.jar

