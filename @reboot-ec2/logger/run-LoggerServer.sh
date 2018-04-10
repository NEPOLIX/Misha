#!/bin/bash
git pull origin develop; rm nohup.out ;gradle build misha;sleep 0.5;kill $(ps aux | grep '[j]ava' | awk '{print $2}');sleep 1;nohup sh spawn.sh java -server -Xms100M -Xmx2000M -XX:+UseConcMarkSweepGC -XX:+AlwaysPreTouch -XX:+UnlockExperimentalVMOptions -XX:MaxGCPauseMillis=50 -XX:CMSInitiatingOccupancyFraction=70 -XX:+CMSScavengeBeforeRemark -XX:+AggressiveOpts -XX:G1HeapRegionSize=4M -XX:TargetSurvivorRatio=90 -XX:G1NewSizePercent=50 -XX:G1MaxNewSizePercent=80 -XX:InitiatingHeapOccupancyPercent=10 -XX:G1MixedGCLiveThresholdPercent=50 -XX:+AggressiveOpts -jar build/LoggerServer-1.0.jar &
