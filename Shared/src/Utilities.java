import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Utilities
{
    public static Properties LoadProperties(String filename)
    {
        File configFile = new File(filename);
        Properties properties;

        try(FileReader reader = new FileReader(configFile);)
        {
            properties = new Properties();
            properties.load(reader);
        }
        catch (IOException e) { throw new RuntimeException(e); }

        return properties;
    }
}
