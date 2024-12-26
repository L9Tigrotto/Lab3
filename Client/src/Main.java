
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

public class Main
{
    private static final String CONFIG_FILENAME = "client.properties";

    public static void main(String[] args)
    {
        Properties properties = Utilities.LoadProperties(CONFIG_FILENAME);

        /*
        String ServerIP = properties.getProperty("ServerIP");
        int ServerPort = Integer.parseInt(properties.getProperty("ServerPort"));
        try (Socket socket = new Socket(ServerIP, ServerPort))
        {

        }
        catch (IOException e)
        {

        }
        */
    }
}
