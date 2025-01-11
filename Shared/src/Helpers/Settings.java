
package Helpers;

import java.io.*;
import java.util.Properties;

/**
 * This class represents a base class for loading settings from a configuration file.
 * Subclasses can extend this class and override the Load method to handle specific settings.
 */
public abstract class Settings
{
    // TCP/IP address of the server
    public String TCP_IP;

    // TCP port of the server
    public int TCP_PORT;

     // the filename of the configuration file
    private final String _filename;

    // the loaded properties from the configuration file
    private final Properties _properties;

    /**
     * Constructor that takes the configuration filename as input.
     *
     * @param filename The name of the configuration file.
     * @throws IOException If an error occurs while reading the configuration file.
     */
    public Settings(String filename) throws IOException
    {
        _filename = filename;
        File configFile = new File(filename);
        try (FileReader reader = new FileReader(configFile))
        {
            _properties = new Properties();
            _properties.load(reader);

            // call the Load method to parse and store the settings
            Load(_properties);
        }
    }

    /**
     * Protected method for subclasses to handle specific settings.
     * This method is called by the constructor to load settings.
     *
     * @param properties The Properties object containing the loaded settings.
     */
    protected void Load(Properties properties)
    {
        TCP_IP = properties.getProperty("TCP_IP");
        TCP_PORT = Integer.parseInt(properties.getProperty("TCP_PORT"));
    }

    /**
     * Saves the current server configuration settings to the configuration file.
     *
     * @throws IOException If an error occurs while saving the configuration file.
     */
    public void Save() throws IOException
    {
        Update(_properties);
        File configFile = new File(_filename);
        try (FileWriter writer = new FileWriter(configFile)) { _properties.store(writer, ""); }
    }

    protected abstract void Update(Properties properties);
}
