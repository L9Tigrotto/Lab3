#!/bin/bash

sharedCompilePaths="
./Shared/src/*.java
./Shared/src/DataStructures/*.java
./Shared/src/DataStructures/Messages/*.java"

clientCompilePaths="
./Client/src/*.java"

serverCompilePaths="
./Server/src/*.java"

javac -d Build/Client $clientCompilePaths $sharedCompilePaths
javac -d Build/Server $serverCompilePaths $sharedCompilePaths

linkPaths="
./*.class
./DataStructures/*.class
./DataStructures/Messages/*.class"

cd ./Build/Client
jar cfe ../Client.jar Main $linkPaths
cd ../Server
jar cfe ../Server.jar Main $linkPaths
cd ../../

cp "Client/src/client.properties" "Build/client.properties"
cp "Server/src/server.properties" "Build/server.properties"