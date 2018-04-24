@echo off
cd /d %~sdp0
java -jar .\build\libs\middle-ground-service-Register-0.0.1-SNAPSHOT.jar --spring.profiles.active=node-1
exit