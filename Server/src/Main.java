
import DataStructures.User;

import java.io.IOException;

public class                                                                                                                     Main
{
    public static void main(String[] args) throws IOException
    {
        User user = new User("John Doe", "123");
        User.Save();

        /*
        Listener listener = new Listener();
        listener.Start();

        Scanner scanner = new Scanner(System.in);
        while (!scanner.nextLine().equalsIgnoreCase("stop")) { }
        listener.Stop();
        */
    }


}
