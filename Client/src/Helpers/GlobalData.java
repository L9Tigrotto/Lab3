package Helpers;

import java.io.IOException;

/**
 * This class holds global data and settings for the client application.
 */
public class GlobalData
{
    // the filename of the client configuration file
    private static final String CONFIG_FILENAME = "client.properties";

    // the client settings object, loaded from the configuration file
    public static final ClientSettings SETTINGS;

    static
    {
        try { SETTINGS = new ClientSettings(CONFIG_FILENAME); }
        catch (IOException e) { throw new RuntimeException(e); }
    }
}
