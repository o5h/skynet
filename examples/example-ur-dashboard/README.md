
* Build

```
mvn -pl . clean compile assembly:single
```

* Run
```
java -jar ./target/example-ur-dashboard-1.0-SNAPSHOT-jar-with-dependencies.jar  <hostname>
```

Output will be something like:
```
INFO :Connected: Universal Robots Dashboard Server
DEBUG:Request: 'power on'
DEBUG:Response: 'Powering on'
DEBUG:Request: 'brake release'
DEBUG:Response: 'Brake releasing'
DEBUG:Request: 'load default.urcap'
DEBUG:Response: 'File not found: default.urcap'
DEBUG:Request: 'play'
DEBUG:Response: 'Failed to execute: play'
DEBUG:Request: 'programState'
DEBUG:Response: 'STOPPED <unnamed>'
Program state is  STOPPED
DEBUG:Request: 'quit'
DEBUG:Response: 'Disconnected'
```