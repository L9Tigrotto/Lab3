
import java.util.Properties;

public class Settings
{
    public int TCPPort;
    public int MaxHandledClients;
    public int TimeoutMS;

    public Settings(String filename)
    {
        Properties properties = Utilities.LoadProperties(filename);

        TCPPort = Integer.parseInt(properties.getProperty("TCPPort"));
        MaxHandledClients = Integer.parseInt(properties.getProperty("MaxHandledClients"));
        TimeoutMS = Integer.parseInt(properties.getProperty("TimeoutMS"));
    }
}
