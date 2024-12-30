
import java.util.Scanner;

public class                                                                                                                     Main
{
    private static final String CONFIG_FILENAME = "server.properties";
    public static final Settings SETTINGS = new Settings(CONFIG_FILENAME);

    public static void main(String[] args) throws InterruptedException
    {
        Listener listener = new Listener();
        listener.Start();

        Scanner scanner = new Scanner(System.in);
        while (!scanner.nextLine().equalsIgnoreCase("stop")) {  }
        listener.Stop();
    }


}
