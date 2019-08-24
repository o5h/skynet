# SkyNet

## Q&A

### What is SkyNet? 
 Set of URCaps and examples for [UR+ platform](https://www.universal-robots.com/plus/)


### Precondition
* Become a [UR+ Developer](https://www.universal-robots.com/plus/developer/)
* Have [URCaps SDK](https://plus.universal-robots.com/download-center/urcaps-sdk/)  artifacts installed
```
call mvn install:install-file -Dfile=artifacts/api/1.7.0/com.ur.urcap.api-1.7.0.jar -DgroupId=com.ur.urcap -DartifactId=api -Dversion=1.7.0 -Dpackaging=jar -q
call mvn install:install-file -Dfile=artifacts/api/1.7.0/com.ur.urcap.api-1.7.0-javadoc.jar -DgroupId=com.ur.urcap -DartifactId=api -Dversion=1.7.0 -Dpackaging=jar -Dclassifier=javadoc -q
call mvn install:install-file -Dfile=artifacts/api/1.7.0/com.ur.urcap.api-1.7.0-sources.jar -DgroupId=com.ur.urcap -DartifactId=api -Dversion=1.7.0 -Dpackaging=jar -Dclassifier=sources -q
call mvn install:install-file -Dfile=artifacts/archetype/com.ur.urcap.archetype-1.7.0.jar -DgroupId=com.ur.urcap -DartifactId=archetype -Dversion=1.7.0 -Dpackaging=jar -q
```
* For testing purposes you should have [URCaps Starter Package](https://plus.universal-robots.com/download-center/urcaps-starter-package/) with Universal Robots offline PolyScope simulator or real robot.
* Java 1.6 or better
 
---
##Q/A 
### Setup ssh server
```
sudo apt update
sudo apt install openssh-server
sudo systemctl status ssh

```