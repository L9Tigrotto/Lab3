
import Helpers.GlobalData;
import Helpers.Utilities;
import Networking.Connection;
import Networking.RequestHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * This class represents the main entry point for the client application. It establishes a connection to the server,
 * reads user input in a loop, and processes the user commands by delegating them to the appropriate methods
 * in the RequestHandler class.
 */
public class Main
{
    public static void main(String[] args)
    {
        // attempt to connect to the server
        Socket socket;
        try { socket = Utilities.TryConnect(
                GlobalData.SETTINGS.TCP_IP,
                GlobalData.SETTINGS.TCP_PORT,
                GlobalData.SETTINGS.ConnectionRetries,
                GlobalData.SETTINGS.ConnectionRetryTimeoutMS); }
        catch (Exception e) { return; }

        // create a connection object
        Connection connection;
        try { connection = new Connection(socket); }
        catch (IOException e) { System.err.printf("[ERROR] Unable to create connection: %s\n", e.getMessage()); return; }

        String temp = "login Leo 1234";
        RequestHandler.SendLogin(connection, temp.split(" "));

        temp = "insertLimitOrder bid 800 2200";
        RequestHandler.SendInsertLimitOrder(connection, temp.split(" "));

        // print the available options to the user
        PrintOptions();
        boolean isConnectionAlive = true;
        while (isConnectionAlive)
        {
            // read user input, remove leading/trailing whitespaces, split into words and extract the
            // command from the first word
            String input = GetStringInput().trim();
            String[] words = input.split(" ");
            String command = words[0];

            // handle different commands
            if (command.equalsIgnoreCase("exit")) { break; }
            else if (command.equalsIgnoreCase("help")) { PrintOptions(); }
            else if (command.equalsIgnoreCase("register")) { isConnectionAlive = RequestHandler.SendRegister(connection, words); }
            else if (command.equalsIgnoreCase("updateCredentials")) { isConnectionAlive = RequestHandler.SendUpdateCredentials(connection, words); }
            else if (command.equalsIgnoreCase("login")) { isConnectionAlive = RequestHandler.SendLogin(connection, words); }
            else if (command.equalsIgnoreCase("logout")) { isConnectionAlive = RequestHandler.SendLogout(connection, words); }
            else if (command.equalsIgnoreCase("insertMarketOrder")) { isConnectionAlive = RequestHandler.SendInsertMarketOrder(connection, words); }
            else if (command.equalsIgnoreCase("insertLimitOrder")) { isConnectionAlive = RequestHandler.SendInsertLimitOrder(connection, words); }
            else if (command.equalsIgnoreCase("insertStopOrder")) { isConnectionAlive = RequestHandler.SendInsertStopOrder(connection, words); }
            else if (command.equalsIgnoreCase("cancelOrder")) { isConnectionAlive = RequestHandler.SendCancelOrder(connection, words); }
            else { System.out.println("[WARNING] Unknown command. 'help' to see options."); }

            // check if connection with the server is still alive
            if (!isConnectionAlive) { System.out.println("[ERROR] Connection closed by the server"); }
        }

        if (isConnectionAlive)
        {
            try { connection.Close(); }
            catch (IOException e) { System.err.printf("[ERROR] Unable to close the connection properly: %s\n", e.getMessage()); }
        }

        // save settings
        GlobalData.Save();
    }

    /**
     * Prints the available options to the user.
     */
    private static void PrintOptions()
    {
        System.out.println("a) 'exit' to exit");
        System.out.println("b) 'help' print options");
        System.out.println("1) 'register <username> <password>' to register a new user");
        System.out.println("2) 'updateCredentials <username> <oldPassword> <newPassword>' to update credentials");
        System.out.println("3) 'login <username> <password>' to login");
        System.out.println("4) 'logout <username>' to logout");
        System.out.println("5) 'insertMarketOrder <type> <size>' to insert a market order");
        System.out.println("6) 'insertLimitOrder <type> <size> <limit>' to insert a limit order");
        System.out.println("7) 'insertStopOrder <type> <size> <stopPrice>' to insert a stop order");
        System.out.println("8) 'cancelOrder <orderID>' to cancel an order");
    }

    /**
     * Reads a line of input from the user.
     *
     * @return The user's input as a string.
     */
    private static String GetStringInput()
    {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
