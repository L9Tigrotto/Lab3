
import Messages.LoginRequest;
import Messages.RegisterRequest;
import Messages.SimpleResponse;
import Messages.UpdateCredentialsRequest;
import Network.Connection;
import Network.Request;

import java.io.IOException;

public class RequestHandler
{
    private static boolean SendAndWaitSimpleResponse(Connection connection, Request request)
    {
        try { connection.Send(request); }
        catch (IOException e) { return false; }

        SimpleResponse response;
        try { response = (SimpleResponse) connection.ReceiveResponse(); }
        catch (IOException e) { return false; }

        System.out.printf("Code: %d, message: %s\n", response.GetResponse(), response.GetErrorMessage());
        return true;
    }

    public static boolean SendRegister(Connection connection, String[] words)
    {
        if (words.length != 3)
        {
            System.out.println("Usage: register <username> <password>");
            return true;
        }

        String username = words[1];
        String password = words[2];
        RegisterRequest register = new RegisterRequest(username, password);

        return SendAndWaitSimpleResponse(connection, register);
    }

    public static boolean SendUpdateCredentials(Connection connection, String[] words)
    {
        if (words.length != 4)
        {
            System.out.println("Usage: updateCredentials <username> <oldPassword> <newPassword>");
            return true;
        }

        String username = words[1];
        String oldPassword = words[2];
        String newPassword = words[3];
        UpdateCredentialsRequest updateCredentials = new UpdateCredentialsRequest(username, oldPassword, newPassword);

        return SendAndWaitSimpleResponse(connection, updateCredentials);
    }

    public static boolean SendLogin(Connection connection, String[] words)
    {
        if (words.length != 3)
        {
            System.out.println("Usage: login <username> <password>");
            return true;
        }

        String username = words[1];
        String password = words[2];
        LoginRequest login = new LoginRequest(username, password);

        return SendAndWaitSimpleResponse(connection, login);
    }
}
