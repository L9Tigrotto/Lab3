
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
        GlobalData.TCP_LISTENER.Start();

        System.out.println("[INFO] Server started");

        System.out.println("a) 'stop' to stop the server");
        System.out.println("b) 'help' print options");

        // create a scanner to read user input for server termination
        try (Scanner scanner = new Scanner(System.in))
        {
            // wait for the user to enter 'stop'
            while (true)
            {
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("stop")) { break; }
                else if (input.equalsIgnoreCase("help")) { PrintOptions(); }
                if (input.equalsIgnoreCase("status")) { System.out.println(OrderBook.PrintStatus()); }
            }
        }

        // stop the listener thread to prevent accepting new connections
        GlobalData.TCP_LISTENER.Stop();

        // save any server data before exiting
        GlobalData.Save();

        System.out.println("[INFO] Server stopped successfully");
    }

    /**
     * Prints the available options.
     */
    private static void PrintOptions()
    {
        System.out.println("a) 'stop' to stop the server");
        System.out.println("b) 'help' print options");
        System.out.println("c) 'status' to print the order book status");
    }
}
