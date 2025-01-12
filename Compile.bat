mkdir ".\Build\Libraries"
mkdir ".\Build\Client\META-INF"
mkdir ".\Build\Server\META-INF"
copy ".\Shared\Libraries\gson-2.11.0.jar" ".\Build\Libraries\gson-2.11.0.jar"
copy ".\Client\src\META-INF\MANIFEST.MF" ".\Build\Client\META-INF\MANIFEST.MF"
copy ".\Server\src\META-INF\MANIFEST.MF" ".\Build\Server\META-INF\MANIFEST.MF"

set sharedCompilePaths=^
    .\Shared\src\Helpers\*.java^
    .\Shared\src\Messages\*.java^
    .\Shared\src\Networking\*.java^
    .\Shared\src\Orders\*.java^
    .\Shared\src\Users\*.java

set clientCompilePaths=^
    .\Client\src\*.java^
    .\Client\src\Helpers\*.java^
    .\Client\src\Networking\*.java

set serverCompilePaths=^
    .\Server\src\*.java^
    .\Server\src\Helpers\*.java^
    .\Server\src\Networking\*.java

set librariesCompilePaths=^
    ".\Shared\Libraries\gson-2.11.0.jar"

javac -cp %librariesCompilePaths% -d .\Build\Client %clientCompilePaths% %sharedCompilePaths%
javac -cp %librariesCompilePaths% -d .\Build\Server %serverCompilePaths% %sharedCompilePaths%

set clientLinkPaths=^
    .\*.class^
    .\Helpers\*.class^
    .\Messages\*.class^
    .\Networking\*.class^
    .\Orders\*.class^
    .\Users\*.class

set serverLinkPaths=^
    .\*.class^
    .\Helpers\*.class^
    .\Messages\*.class^
    .\Networking\*.class^
    .\Orders\*.class^
    .\Users\*.class

cd .\Build\Client
jar cmf .\META-INF\MANIFEST.MF ..\Client.jar %clientLinkPaths%

cd ..\Server

jar cmf .\META-INF\MANIFEST.MF ..\Server.jar %serverLinkPaths%
cd ..\..\

copy ".\Client\src\client.properties" ".\Build\client.properties"
copy ".\Server\src\server.properties" ".\Build\server.properties"