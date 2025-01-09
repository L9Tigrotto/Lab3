package Helpers;

import java.io.IOException;
import java.util.Properties;

/**
 * This class represents server settings loaded from a configuration file.
 */
public class ServerSettings extends Settings
{
    // maximum number of clients the server can handle concurrently
    public int MaxConcurrentClients;

    // timeout in milliseconds before checking if the server is closing
    public int AcceptClientTimeoutMS;

    // timeout in milliseconds for waiting data from connected clients before checking of the server is closing
    public int WaitDataTimeoutMS;

    // time in milliseconds to determinate if client is active or inactive
    public int ClientInactiveThresholdMS;

    /**
     * Constructor that takes the configuration filename as input.
     * @param filename The name of the configuration file.
     * @throws IOException If there's an error reading the configuration file.
     */
    public ServerSettings(String filename) throws IOException { super(filename); }

    /**
     * Override the Load method from the base class to handle specific server settings.
     * This method is called after the base class loads general settings.
     * @param properties The Properties object loaded from the configuration file.
     */
    @Override
    protected void Load(Properties properties)
    {
        // call the base class Load method first, then parse and store server-specific settings
        // from the properties object
        super.Load(properties);

        MaxConcurrentClients = Integer.parseInt(properties.getProperty("MaxConcurrentClients"));
        AcceptClientTimeoutMS = Integer.parseInt(properties.getProperty("AcceptClientTimeoutMS"));
        WaitDataTimeoutMS = Integer.parseInt(properties.getProperty("WaitDataTimeoutMS"));
        ClientInactiveThresholdMS = Integer.parseInt(properties.getProperty("ClientInactiveThresholdMS"));
    }
}
