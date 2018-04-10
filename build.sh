#!/bin/sh
mkdir /home/ubuntu/;
mkdir /home/ubuntu/.misha/;
mkdir ~/.misha/;
echo MAKE TARGET DIRECTORY;
mkdir -p target/lib;
#
echo ;echo BUILD Misha;echo ;echo WAIT;sleep 0.5s;echo ;
#
#
echo COPY FINAL BUILDS OF CLASSES;
cp -a MishaScheduler/build/classes/main/. target/;
cp -a Jigsaw/build/classes/main/. target/;
cp -a MishaTaskHandler/build/classes/main/. target/;
cp -a MishaDB/build/classes/main/. target/;
cp -a MishaDB/MishaCache/build/classes/main/. target/;
cp -a MishaDB/MishaDBCold/build/classes/main/. target/;
cp -a MishaDB/MishaNoSqlDB/build/classes/main/. target/;
cp -a MishaID/build/classes/main/. target/;
cp -a MishaID/MIDClient/build/classes/main/. target/;
cp -a MishaID/MIDCommon/build/classes/main/. target/;
cp -a MJSON/build/classes/main/. target/;
cp -a WebClient/build/classes/main/. target/;
cp -a MishaLogger/build/classes/main/. target/;
cp -a MishaLogger/LoggerCommon/build/classes/main/. target/;
cp -a MishaLogger/Logger/build/classes/main/. target/;
cp -a MishaLogger/LoggerViewer/build/classes/main/. target/;
cp -a MishaServer/build/classes/main/. target/;
cp -a MishaServer/RESTServer/build/classes/main/. target/;
cp -a MishaServer/SocketServer/build/classes/main/. target/;
echo  ;
#
#       COPY MODULES JARS
echo COPY MODULES JARS;
cp -a Jigsaw/lib/. target/lib;
cp -a MishaScheduler/lib/. target/lib;
cp -a MishaTaskHandler/lib/. target/lib;
cp -a MishaID/lib/. target/lib;
cp -a MishaID/MIDClient/lib/. target/lib;
cp -a MishaID/MIDCommon/lib/. target/lib;
cp -a MishaDB/MishaDBCold/lib/. target/lib;
cp -a MJSON/lib/. target/lib;
cp -a MishaServer/lib/. target/lib;
cp -a MishaServer/RESTServer/lib/. target/lib;
cp -a MishaServer/SocketServer/lib/. target/lib;
cp -a WebClient/lib/. target/lib;
cp -a MishaLogger/lib/. target/lib;
#
#
echo MOVE TARGET TO BUILD;
mv target build;
echo ALL DONE;
echo  ;echo  ;echo   ;
cp MishaScheduler/*.config /home/ubuntu/.misha/;
cp MishaDB/*.config /home/ubuntu/.misha/;
cp MishaDB/MishaCache/*.config /home/ubuntu/.misha/;
cp Jigsaw/*.config /home/ubuntu/.misha/;
cp MishaTaskHandler/*.config /home/ubuntu/.misha/;
cp MishaID/*.config /home/ubuntu/.misha/;
cp MJSON/*.config /home/ubuntu/.misha/;
cp MishaServer/*.config /home/ubuntu/.misha/;
cp MishaServer/RESTServer/*.config /home/ubuntu/.misha/;
cp MishaServer/SocketServer/*.config /home/ubuntu/.misha/;
cp WebClient/*.config /home/ubuntu/.misha/;
cp MishaLogger/*.config /home/ubuntu/.misha/;

#
#       COPY HTTPS cert to .misha/cert/
mkdir /home/ubuntu/.misha/cert/;
cp MishaServer/RESTServer/src/main/java/com/nepolix/misha/web/engine/server/misha.jks /home/ubuntu/.misha/cert/;
cp MishaServer/RESTServer/src/main/java/com/nepolix/misha/web/engine/server/misha.cer /home/ubuntu/.misha/cert/;
#
#
#        MOVE MODULES JARS TO BUILD
echo MOVE MODULES JARS TO BUILD;
mv MishaDB/MishaCache/build/libs/* build/;
mv MishaDB/MishaDBCold/build/libs/* build/;
mv MishaID/build/libs/* build/;
mv MishaLogger/LoggerViewer/build/libs/* build/;
#
#       REMOVE MAIN BUILDS
echo REMOVE MAIN BUILDS;
rm -rf MishaScheduler/build;
rm -rf MishaScheduler/out;
rm -rf Jigsaw/build;
rm -rf Jigsaw/out;
rm -rf MishaTaskHandler/build;
rm -rf MishaTaskHandler/out;
rm -rf MishaDB/build;
rm -rf MishaDB/out;
rm -rf MishaDB/MishaCache/build;
rm -rf MishaDB/MishaCache/out;
rm -rf MishaDB/MishaDBCold/build;
rm -rf MishaDB/MishaDBCold/out;
rm -rf MishaDB/MishaNoSqlDB/build;
rm -rf MishaDB/MishaNoSqlDB/out;
rm -rf MishaID/build;
rm -rf MishaID/out;
rm -rf MishaID/MIDClient/build;
rm -rf MishaID/MIDClient/out;
rm -rf MishaID/MIDCommon/build;
rm -rf MishaID/MIDCommon/out;
rm -rf MJSON/build;
rm -rf MJSON/out;
rm -rf MishaServer/build;
rm -rf MishaServer/out;
rm -rf MishaServer/RESTServer/build;
rm -rf MishaServer/RESTServer/out;
rm -rf MishaServer/SocketServer/build;
rm -rf MishaServer/SocketServer/out;
rm -rf WebClient/build;
rm -rf WebClient/out;
rm -rf MishaLogger/build;
rm -rf MishaLogger/out;
rm -rf MishaLogger/LoggerCommon/build;
rm -rf MishaLogger/LoggerCommon/out;
rm -rf MishaLogger/Logger/build;
rm -rf MishaLogger/Logger/out;
rm -rf MishaLogger/LoggerViewer/build;
rm -rf MishaLogger/LoggerViewer/out;
rm -rf MishaGlobal/build;
rm -rf MishaGlobal/out;
#
#
#echo ======================================================================
#echo "java -cp \".:build/:build/lib/*\" <package.--.package.Main-Class>"
#echo ======================================================================
echo ======================== BACKGROUND PROCESS ==========================
echo "nohup  java -jar build/THE-JAR.jar &"
echo ======================================================================;
rm -rf build/lib;rm -rf build/com;