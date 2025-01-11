
package Helpers;

import java.io.IOException;

import java.util.Properties;

/**
 * This class represents client settings loaded from a configuration file.
 */
public class ClientSettings extends Settings
{
    // maximum number of retries for connection attempts
    public int ConnectionRetries;

    // timeout in milliseconds between connection retries
    public int ConnectionRetryTimeoutMS;

    /**
     * Constructor that takes the configuration filename as input.
     * @param filename The name of the configuration file.
     * @throws IOException If there's an error reading the configuration file.
     */
    public ClientSettings(String filename) throws IOException { super(filename); }

    /**
     * Override the Load method from the base class to handle specific client settings.
     * This method is called after the base class loads general settings.
     * @param properties The Properties object loaded from the configuration file.
     */
    @Override
    public void Load(Properties properties)
    {
        // call the base class Load method first, then parse and store client-specific settings
        super.Load(properties);

        ConnectionRetries = Integer.parseInt(properties.getProperty("ConnectionRetries"));
        ConnectionRetryTimeoutMS = Integer.parseInt(properties.getProperty("ConnectionRetryTimeoutMS"));
    }

    @Override
    protected void Update(Properties properties) { }
}
