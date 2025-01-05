
import DataStructures.Settings;

import java.io.IOException;
import java.util.Properties;

public class ServerSettings extends Settings
{
    public int MaxHandledClients;
    public int AcceptTimeoutMS;

    public ServerSettings(String filename) throws IOException { super(filename); }

    @Override
    public void Load(Properties properties)
    {
        super.Load(properties);
        MaxHandledClients = Integer.parseInt(properties.getProperty("MaxHandledClients"));
        AcceptTimeoutMS = Integer.parseInt(properties.getProperty("AcceptTimeoutMS"));
    }
}
