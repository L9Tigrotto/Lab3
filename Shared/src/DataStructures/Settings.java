
package DataStructures;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Settings
{
    public String TCP_IP;
    public int TCP_PORT;

    public Settings(String filename) throws IOException
    {
        File configFile = new File(filename);
        FileReader reader = new FileReader(configFile);

        Properties properties = new Properties();
        properties.load(reader);

        Load(properties);
    }

    public void Load(Properties properties)
    {
        TCP_IP = properties.getProperty("TCP_IP");
        TCP_PORT = Integer.parseInt(properties.getProperty("TCP_PORT"));
    }
}
