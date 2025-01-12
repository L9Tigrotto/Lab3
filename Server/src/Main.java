
import Helpers.GlobalData;
import Orders.OrderBook;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class represents the main entry point for the server application. It starts a listener thread to
 * handle incoming connections and prompts the user to enter 'stop' to terminate the server.
 */
public class Main
{
    public static void main(String[] args) throws IOException
    {
        // start the listener thread to accept connections from clients
        GlobalData.LISTENER.Start();

        System.out.println("[INFO] Server started, enter 'stop' to stop the server");

        // create a scanner to read user input for server termination
        try (Scanner scanner = new Scanner(System.in))
        {
            // wait for the user to enter 'stop'
            while (true)
            {
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("stop")) { break; }
                if (input.equalsIgnoreCase("status"))
                {
                    System.out.println(OrderBook.PrintStatus());
                }
            }
        }

        // stop the listener thread to prevent accepting new connections
        GlobalData.LISTENER.Stop();

        // save any server data before exiting
        GlobalData.Save();

        System.out.println("[INFO] Server stopped successfully");
    }
}
