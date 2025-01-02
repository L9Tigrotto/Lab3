
set sharedCompilePaths=^
    .\Shared\src\*.java^
    .\Shared\src\DataStructures\*.java^
    .\Shared\src\Messages\*.java^
    .\Shared\src\Messages\Requests\*.java^
    .\Shared\src\Messages\Responses\*.java^
    .\Shared\src\Network\*.java

set clientCompilePaths=^
    .\Client\src\*.java

set serverCompilePaths=^
    .\Server\src\*.java

set librariesCompilePaths=^
    .\Shared\Libraries\*;

javac -d .\Build\Client -cp %librariesCompilePaths%  %clientCompilePaths% %sharedCompilePaths%
javac -d .\Build\Server -cp %librariesCompilePaths% %serverCompilePaths% %sharedCompilePaths%


set linkPaths=^
    .\*.class^
	.\DataStructures\*.class^
	.\Messages\*.class^
	.\Messages\Requests\*.class^
	.\Messages\Responses\*.class^
	.\Network\*.class

cd .\Build\Client
jar cfe ..\Client.jar Main %linkPaths%
cd ..\Server
jar cfe ..\Server.jar Main %linkPaths%
cd ..\..\

copy ".\Client\src\client.properties" ".\Build\client.properties"
copy ".\Server\src\server.properties" ".\Build\server.properties"