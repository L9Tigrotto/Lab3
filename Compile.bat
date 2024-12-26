set mylist=^
    ./Shared/src/*.java^
    ./Shared/src/DataStructures/*.java^
    ./Shared/src/DataStructures/Messages/*.java

javac -d Build/Client ./Client/src/Main.java %mylist%
javac -d Build/Server ./Server/src/Main.java %mylist%

cd ./Build/Client
jar cfe ../Client.jar Main *.class
cd ../Server
jar cfe ../Server.jar Main *.class
cd ../../

copy ".\Client\src\client.properties" ".\Build\client.properties"
copy ".\Server\src\server.properties" ".\Build\server.properties"