
import DataStructures.Settings;

import java.io.IOException;
import java.util.Properties;

public class ServerSettings extends Settings
{
    public int TCPPort;
    public int MaxHandledClients;
    public int TimeoutMS;

    public ServerSettings(String filename) throws IOException { super(filename); }

    @Override
    public void Load(Properties properties)
    {
        super.Load(properties);
        MaxHandledClients = Integer.parseInt(properties.getProperty("MaxHandledClients"));
        TimeoutMS = Integer.parseInt(properties.getProperty("TimeoutMS"));
    }
}
