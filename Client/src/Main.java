
import Network.Connection;

import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

public class Main
{
    private static final String CONFIG_FILENAME = "client.properties";
    public static final ClientSettings SETTINGS;

    static
    {
        try { SETTINGS = new ClientSettings(CONFIG_FILENAME); }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    public static void main(String[] args) throws IOException
    {
        Socket socket;
        try { socket = Utilities.TryConnect(
                SETTINGS.TCP_IP,
                SETTINGS.TCP_PORT,
                SETTINGS.ConnectionRetries,
                SETTINGS.ConnectionRetryTimeoutMS); }
        catch (Exception e) { return; }

        Connection connection = new Connection(socket);
        int number = 0;
        while (true)
        {
            connection.Send(number);
            number = connection.ReceiveInt() + 1;
            System.out.printf("[INFO] Received %d from server.\n", number);
        }
    }
}
