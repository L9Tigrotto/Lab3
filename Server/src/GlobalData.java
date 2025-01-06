
import java.io.IOException;

/**
 * This class holds global data and settings for the server application.
 */
public class GlobalData
{
    // the filename of the server configuration file
    private static final String CONFIG_FILENAME = "server.properties";

    // the server settings object, loaded from the configuration file
    public static final ServerSettings SETTINGS;

    // the listener object for handling incoming connections
    public static final Listener LISTENER;

    static
    {
        try { SETTINGS = new ServerSettings(CONFIG_FILENAME); }
        catch (IOException e) { throw new RuntimeException(e); }

        LISTENER = new Listener();
    }
}
