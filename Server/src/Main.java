
import java.util.Properties;
import java.util.Scanner;

public class                                                                                                                     Main
{
    private static final String CONFIG_FILENAME = "server.properties";

    public static void main(String[] args) throws InterruptedException
    {
        Properties properties = Utilities.LoadProperties(CONFIG_FILENAME);

        int port = Integer.parseInt(properties.getProperty("TCPListenerPort"));
        Listener listener = new Listener(port);
        listener.Start();

        Scanner scanner = new Scanner(System.in);
        while (!scanner.nextLine().equalsIgnoreCase("exit")) {  }
        listener.Stop();
    }


}
