#!/usr/bin/env bash
mvn install:install-file -DgroupId=com.microsoft.jdbc.sqlserver2000 -DartifactId=msbase -Dversion=0.1 -Dpackaging=jar -Dfile=msbase.jar
mvn install:install-file -DgroupId=com.microsoft.jdbc.sqlserver2000 -DartifactId=mssqlserver -Dversion=0.1 -Dpackaging=jar -Dfile=mssqlserver.jar
mvn install:install-file -DgroupId=com.microsoft.jdbc.sqlserver2000 -DartifactId=msutil -Dversion=0.1 -Dpackaging=jar -Dfile=msutil.jar