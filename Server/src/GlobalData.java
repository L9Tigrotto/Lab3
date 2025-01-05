
import java.io.IOException;

public class GlobalData
{
    private static final String CONFIG_FILENAME = "server.properties";
    public static final ServerSettings SETTINGS;
    public static final Listener LISTENER;

    static
    {
        try { SETTINGS = new ServerSettings(CONFIG_FILENAME); }
        catch (IOException e) { throw new RuntimeException(e); }

        LISTENER = new Listener();
    }
}
