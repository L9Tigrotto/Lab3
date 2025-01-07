
import Network.Connection;

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
        boolean connected = true;
        while (true)
        {
            String input = GetStringInput();

            if (input.equalsIgnoreCase("exit")) { break; }
            else if (input.equalsIgnoreCase("help")) { PrintOptions(); }
            else if (input.startsWith("register")) { connected = RequestHandler.SendRegister(connection, input); }
            else if (input.startsWith("updateCredentials")) { connected = RequestHandler.SendUpdateCredentials(connection, input); }
            else { System.out.println("Unknown command. 'help' to see options."); }

            if (!connected)
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
    }

    private static String GetStringInput()
    {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
