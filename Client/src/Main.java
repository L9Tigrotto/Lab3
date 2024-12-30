
import Network.Connection;

import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

public class Main
{
    private static final String CONFIG_FILENAME = "client.properties";

    public static void main(String[] args) throws IOException
    {
        Properties properties = Utilities.LoadProperties(CONFIG_FILENAME);

        String ServerIP = properties.getProperty("ServerIP");
        int ServerPort = Integer.parseInt(properties.getProperty("ServerPort"));
        int tries = 3;
        int timeout = 15 * 1000;

        Socket socket;
        try { socket = Utilities.TryConnect(ServerIP, ServerPort, tries, timeout); }
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
