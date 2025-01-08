
import Messages.*;
import Network.Connection;
import Network.Request;
import Network.Response;

import java.io.IOException;

public class RequestHandler
{
    private static String _username = "";

    private static void PrintSimpleResponse(SimpleResponse response)
    {
        System.out.printf("Code: %d, message: %s\n", response.GetResponse(), response.GetErrorMessage());
    }

    private static void PrintOrderResponse(SimpleResponse response)
    {
        System.out.printf("Code: %d, message: %s\n", response.GetResponse(), response.GetErrorMessage());
    }

    private static SimpleResponse SendAndWaitSimpleResponse(Connection connection, Request request)
    {
        try { connection.Send(request); }
        catch (IOException e) { return null; }

        SimpleResponse response;
        try { response = (SimpleResponse) connection.ReceiveResponse(); }
        catch (IOException e) { return null; }

        return response;
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

        SimpleResponse response = SendAndWaitSimpleResponse(connection, register);
        if (response == null) { return false; }

        PrintSimpleResponse(response);
        return true;
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

        SimpleResponse response = SendAndWaitSimpleResponse(connection, updateCredentials);
        if (response == null) { return false; }

        PrintSimpleResponse(response);
        return true;
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

        SimpleResponse response = SendAndWaitSimpleResponse(connection, login);
        if (response == null) { return false; }

        if (response.GetResponse() == LoginRequest.OK.GetResponse()) { _username = username; }
        PrintSimpleResponse(response);
        return true;
    }

    public static boolean SendLogout(Connection connection, String[] words)
    {
        if (words.length != 2)
        {
            System.out.println("Usage: logout <username>");
            return true;
        }

        String username = words[1];
        if (username.equals(_username))
        {
            LogoutRequest logout = new LogoutRequest();

            SimpleResponse response = SendAndWaitSimpleResponse(connection, logout);
            if (response == null) { return false; }

            if (response.GetResponse() == LogoutRequest.OK.GetResponse()) { _username = username; }
            PrintSimpleResponse(response);
            return true;
        }

        SimpleResponse response = LogoutRequest.OTHER_ERROR_CASES;
        PrintSimpleResponse(response);
        return true;
    }
}
