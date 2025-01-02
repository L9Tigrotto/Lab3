import java.io.IOException;

public class GlobalData
{
    private static final String CONFIG_FILENAME = "client.properties";
    public static final ClientSettings SETTINGS;

    static
    {
        try { SETTINGS = new ClientSettings(CONFIG_FILENAME); }
        catch (IOException e) { throw new RuntimeException(e); }
    }
}
