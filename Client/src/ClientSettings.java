import DataStructures.Settings;

import java.io.IOException;

import java.util.Properties;

public class ClientSettings extends Settings
{
    public int ConnectionRetries;
    public int ConnectionRetryTimeoutMS;

    public ClientSettings(String filename) throws IOException { super(filename); }

    @Override
    public void Load(Properties properties)
    {
        super.Load(properties);

        ConnectionRetries = Integer.parseInt(properties.getProperty("ConnectionRetries"));
        ConnectionRetryTimeoutMS = Integer.parseInt(properties.getProperty("ConnectionRetryTimeoutMS"));
    }
}
