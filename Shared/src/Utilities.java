import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Properties;

public class Utilities
{
    public static Socket TryConnect(String ip, int port, int tries, int timeout) throws InterruptedException, SocketException
    {
        boolean connected = false;
        Socket socket = null;
        int tryCount = 0;

        while (!connected && tryCount < tries)
        {
            System.out.printf("[INFO] Trying to connect to %s:%d\n", ip, port);

            try
            {
                socket = new Socket(ip, port);
                connected = true;
            }
            catch (IOException e)
            {
                tryCount++;
                if (tryCount < tries)
                {
                    System.out.printf("[ERROR] Could not connect to %s:%d. Retrying in %d ms.\n", ip, port, timeout);
                    Thread.sleep(timeout);
                }
                else
                {
                    System.out.println("[Error] Failed to connect to server.");
                }
            }
        }

        if (socket == null) { throw new SocketException(); }
        return socket;
    }
}
