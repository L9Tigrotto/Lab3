
package Helpers;

import java.io.*;
import java.util.Properties;

/**
 * This class represents a base class for loading and saving settings from/to a configuration file.
 * Subclasses can extend this class and override the Load method to handle specific settings.
 */
public abstract class Settings
{
    // TCP/IP address of the server
    public String TCP_IP;

    // TCP port of the server
    public int TCP_PORT;

    // UDP port for the client
    public int CLIENT_UDP_PORT;

    // UDP port for the server
    public int SERVER_UDP_PORT;

    // the filename of the configuration file
    private final String _filename;

    // the loaded properties from the configuration file
    private final Properties _properties;

    /**
     * Constructor that loads the configuration settings from a file.
     * It reads the properties from the file and then calls the Load method
     * to parse and store the specific settings.
     *
     * @param filename The name of the configuration file.
     * @throws IOException If an error occurs while reading the configuration file.
     */
    public Settings(String filename) throws IOException
    {
        _filename = filename;
        File configFile = new File(filename);

        // read properties from the configuration file
        try (FileReader reader = new FileReader(configFile))
        {
            // initialize the Properties object and load the file data
            _properties = new Properties();
            _properties.load(reader);

            // call the Load method to handle the specific settings parsing
            Load(_properties);
        }
    }

    /**
     * Protected method that handles the specific settings loading logic.
     * This method is invoked by the constructor after the properties are loaded
     * from the file. It populates the settings fields with the values from
     * the properties.
     *
     * @param properties The Properties object containing the loaded settings.
     */
    protected void Load(Properties properties)
    {
        TCP_IP = properties.getProperty("TCP_IP");
        TCP_PORT = Integer.parseInt(properties.getProperty("TCP_PORT"));
        CLIENT_UDP_PORT = Integer.parseInt(properties.getProperty("CLIENT_UDP_PORT"));
        SERVER_UDP_PORT = Integer.parseInt(properties.getProperty("SERVER_UDP_PORT"));
    }

    /**
     * Saves the current server configuration settings to the configuration file.
     * This method updates the properties with any new values and writes them back
     * to the file.
     *
     * @throws IOException If an error occurs while saving the configuration file.
     */
    public void Save() throws IOException
    {
        // call the abstract Update method to modify the properties before saving
        Update(_properties);
        File configFile = new File(_filename);

        // Create or open the configuration file for writing and store the updated properties to the file
        try (FileWriter writer = new FileWriter(configFile)) { _properties.store(writer, ""); }
    }

    /**
     * This method must be implemented by subclasses to handle any additional
     * properties or settings specific to their requirements. This method is
     * called within the Save method to allow subclasses to update the properties
     * before saving them to the configuration file.
     *
     * @param properties The Properties object containing the settings to update.
     */
    protected abstract void Update(Properties properties);
}
