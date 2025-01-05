
import DataStructures.User;

import java.io.IOException;
import java.util.Scanner;

public class                                                                                                                     Main
{
    public static void main(String[] args) throws IOException
    {
        Listener listener = new Listener();
        listener.Start();

        Scanner scanner = new Scanner(System.in);
        while (!scanner.nextLine().equalsIgnoreCase("stop")) { }

        listener.Stop();
        User.Save();
    }


}
