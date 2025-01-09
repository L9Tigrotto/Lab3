
import Helpers.GlobalData;

import java.io.IOException;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        GlobalData.LISTENER.Start();

        Scanner scanner = new Scanner(System.in);
        while (!scanner.nextLine().equalsIgnoreCase("stop"))
        {
            System.out.println("[Info] Enter 'stop' to stop the server");
        }

        GlobalData.LISTENER.Stop();
        GlobalData.Save();
    }


}
