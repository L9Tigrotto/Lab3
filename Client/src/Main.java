
import Network.Connection;

import java.io.IOException;
import java.net.Socket;

public class Main
{
    public static void main(String[] args) throws IOException, InterruptedException
    {
        Socket socket;
        try { socket = Utilities.TryConnect(
                GlobalData.SETTINGS.TCP_IP,
                GlobalData.SETTINGS.TCP_PORT,
                GlobalData.SETTINGS.ConnectionRetries,
                GlobalData.SETTINGS.ConnectionRetryTimeoutMS); }
        catch (Exception e) { return; }

        Connection connection = new Connection(socket);

        Thread.sleep(2 * 1000);

        connection.Close();
    }
}
