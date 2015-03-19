#!/bin/sh

echo Building jar.
/usr/local/Cellar/ant/1.9.4/libexec/bin/ant

echo Uploading jar...
scp server.jar root@playwolf.us:~/server.jar

echo Restarting server
ssh root@playwolf.us 'screen -S wolf -X quit;screen -S wolf -d -m java -jar server.jar'

echo Done.
