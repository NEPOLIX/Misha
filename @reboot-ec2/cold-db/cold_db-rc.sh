#!/bin/bash
sudo service mysql restart
cd /home/ubuntu/
export JAVA_HOME="/usr/lib/jvm/java-8-oracle"
export GRADLE_HOME="/usr/local/gradle/gradle-4.1"
export PATH=$PATH:$JAVA_HOME/bin:$GRADLE_HOME/bin
sudo /bin/dd if=/dev/zero of=/var/swap.1 bs=1M count=10240
sudo /sbin/mkswap /var/swap.1
sudo chmod 600 /var/swap.1
sudo /sbin/swapon /var/swap.1
eval `ssh-agent -s`
ssh-add .ssh/gitlab_rsa.pem
rm -rf Misha/
git clone git@gitlab.com:NEPOLIX-Genesis/Misha.git
cd Misha
git checkout develop
gradle build misha
sleep 0.5
kill $(ps aux | grep '[j]ava' | awk '{print $2}')
sleep 1
nohup sh spawn.sh java -server -Xms100M -Xmx10000M -XX:+UseConcMarkSweepGC -XX:+AlwaysPreTouch -XX:+UnlockExperimentalVMOptions -XX:MaxGCPauseMillis=50 -XX:CMSInitiatingOccupancyFraction=70 -XX:+CMSScavengeBeforeRemark -XX:+AggressiveOpts -XX:G1HeapRegionSize=4M -XX:TargetSurvivorRatio=90 -XX:G1NewSizePercent=50 -XX:G1MaxNewSizePercent=80 -XX:InitiatingHeapOccupancyPercent=10 -XX:G1MixedGCLiveThresholdPercent=50 -XX:+AggressiveOpts -jar build/MishaDBCold-1.0.jar &
