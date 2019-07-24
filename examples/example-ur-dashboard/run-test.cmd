@echo off
call mvn -pl . clean compile assembly:single
java -jar ./target/example-ur-dashboard-1.0-SNAPSHOT-jar-with-dependencies.jar  192.168.253.128
