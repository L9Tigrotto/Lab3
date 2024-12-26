
import java.util.Properties;

public class Main
{
    private static final String CONFIG_FILENAME = "client.properties";

    public static void main(String[] args)
    {
        Properties properties = Utilities.LoadProperties(CONFIG_FILENAME);

        System.out.println(properties.getProperty("ServerIP"));
    }
}
