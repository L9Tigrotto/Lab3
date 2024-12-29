set sharedCompilePaths=^
    .\Shared\src\*.java^
    .\Shared\src\DataStructures\*.java^
    .\Shared\src\DataStructures\Messages\*.java

set clientCompilePaths=^
    .\Client\src\*.java

set serverCompilePaths=^
    .\Server\src\*.java

javac -d Build\Client %clientCompilePaths% %sharedCompilePaths%
javac -d Build\Server %serverCompilePaths% %sharedCompilePaths%


set linkPaths=^
    .\*.class^
	.\DataStructures\*.class^
	.\DataStructures\Messages\*.class

cd .\Build\Client
jar cfe ..\Client.jar Main %linkPaths%
cd ..\Server
jar cfe ..\Server.jar Main %linkPaths%
cd ..\..\

copy ".\Client\src\client.properties" ".\Build\client.properties"
copy ".\Server\src\server.properties" ".\Build\server.properties"