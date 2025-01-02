
import java.io.IOException;
import java.util.Scanner;

public class                                                                                                                     Main
{
    private static final String CONFIG_FILENAME = "server.properties";
    public static final ServerSettings SETTINGS;

    static
    {
        try { SETTINGS = new ServerSettings(CONFIG_FILENAME); }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    public static void main(String[] args) throws IOException
    {
        Listener listener = new Listener();
        listener.Start();

        Scanner scanner = new Scanner(System.in);
        while (!scanner.nextLine().equalsIgnoreCase("stop")) {  }
        listener.Stop();
    }


}
