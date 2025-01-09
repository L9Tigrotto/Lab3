
import Helpers.GlobalData;
import Helpers.Utilities;
import Networking.Connection;
import Networking.RequestHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        Socket socket;
        try { socket = Utilities.TryConnect(
                GlobalData.SETTINGS.TCP_IP,
                GlobalData.SETTINGS.TCP_PORT,
                GlobalData.SETTINGS.ConnectionRetries,
                GlobalData.SETTINGS.ConnectionRetryTimeoutMS); }
        catch (Exception e) { return; }

        Connection connection;
        try { connection = new Connection(socket); }
        catch (IOException e) { System.err.println(e.getMessage()); return; }

        PrintOptions();
        boolean isConnectionAlive = true;
        while (true)
        {
            String input = GetStringInput().trim();
            String[] words = input.split(" ");
            String command = words[0];

            if (command.equalsIgnoreCase("exit")) { break; }
            else if (command.equalsIgnoreCase("help")) { PrintOptions(); }
            else if (command.equalsIgnoreCase("register")) { isConnectionAlive = RequestHandler.SendRegister(connection, words); }
            else if (command.equalsIgnoreCase("updateCredentials")) { isConnectionAlive = RequestHandler.SendUpdateCredentials(connection, words); }
            else if (command.equalsIgnoreCase("login")) { isConnectionAlive = RequestHandler.SendLogin(connection, words); }
            else if (command.equalsIgnoreCase("logout")) { isConnectionAlive = RequestHandler.SendLogout(connection, words); }
            else { System.out.println("Unknown command. 'help' to see options."); }

            if (!isConnectionAlive)
            {
                System.out.println("[Error] Connection closed by the server");
                break;
            }
        }

        try { connection.Close(); }
        catch (IOException e) { System.err.println(e.getMessage()); }
    }

    private static void PrintOptions()
    {
        System.out.println("a) 'exit' to exit");
        System.out.println("b) 'help' print options");
        System.out.println("1) 'register <username> <password>' to register a new user");
        System.out.println("2) 'updateCredentials <username> <oldPassword> <newPassword>' to update credentials");
        System.out.println("3) 'login <username> <password>' to login");
        System.out.println("4) 'logout' to logout <username>");
    }

    private static String GetStringInput()
    {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
