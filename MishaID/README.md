# Misha ID
MishaID is a universal ID generator that is utilized in [MishaDB](https://github.com/NEPOLIX/Misha/tree/master/MishaDB) module.
Misha ID contains two sub-modules,
* MIDClient, MishaID client with APIs to communicate with the MID server to generate MID.
* MIDCommon, a common utils for MishaID server and MishaID client.

#### MishaID Server
Before deploying MishaID to your server at AWS, you need to setup <code>AWS_ACCESS_KEY</code> and <code>AWS_PRIVATE_KEY</code> in [Credentials](https://github.com/NEPOLIX/Misha/blob/master/MishaID/src/main/java/com/nepolix/misha/id/core/Credentials.java) class.

MishaID use AWS S3 bucket (`misha.id`) as backup to restore the latest `mid`s after server failure. 

###### Sample build/run script for MishaID server
```
#!/bin/bash
git pull origin master; rm nohup.out ;gradle build misha;sleep 0.5;kill $(ps aux | grep '[j]ava' | awk '{print $2}');sleep 1;nohup sh spawn.sh java -server -Xms100M -Xmx1200M -XX:+UseConcMarkSweepGC -XX:+AlwaysPreTouch -XX:+UnlockExperimentalVMOptions -XX:MaxGCPauseMillis=50 -XX:CMSInitiatingOccupancyFraction=70 -XX:+CMSScavengeBeforeRemark -XX:+AggressiveOpts -XX:G1HeapRegionSize=4M -XX:TargetSurvivorRatio=90 -XX:G1NewSizePercent=50 -XX:G1MaxNewSizePercent=80 -XX:InitiatingHeapOccupancyPercent=10 -XX:G1MixedGCLiveThresholdPercent=50 -XX:+AggressiveOpts -jar build/MishaID-1.0.jar &
```

#### MishaID CLient
To use MishaID client you need to set it up by calling `MishaID.initInetAddress()` first and once by passing the URL of _MishaID server_. Afterwards, use `MishaID.getMishaID()` to get its instance.
Use `MishaID.nextID()` and `MishaID.nextKIDs()` to get the next mID or _k_-next mID.