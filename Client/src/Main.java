
import Messages.RegisterRequest;
import Messages.SimpleResponse;
import Network.Connection;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

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

        while (true)
        {
            PrintOptions();
            String input = GetStringInput();
            if (input.equalsIgnoreCase("exit")) { break; }

            boolean connected = true;
            if (input.startsWith("register")) { connected = SendRegistrationRequest(connection, input); }
            else { System.out.println("Unknown command."); }

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
        System.out.println("Enter 'exit' to exit");
        System.out.println("1) 'register <username> <password>' to register a new user");
    }

    private static String GetStringInput()
    {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public static boolean SendRegistrationRequest(Connection connection, String command)
    {
        String[] words = command.split(" ");
        if (words.length < 3)
        {
            System.out.println("Usage: register <username> <password>");
            return true;
        }

        String username = words[1];
        String password = Arrays.stream(words).skip(2).collect(Collectors.joining(" "));
        RegisterRequest registerRequest = new RegisterRequest(username, password);

        try { connection.Send(registerRequest); }
        catch (IOException e) { return false; }

        SimpleResponse response;
        try { response = (SimpleResponse) connection.ReceiveResponse(); }
        catch (IOException e) { return false; }

        System.out.printf("Code: %d, message: %s\n", response.GetResponse(), response.GetErrorMessage());
        return true;
    }
}
