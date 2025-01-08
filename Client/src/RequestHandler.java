
import Messages.*;
import Network.Connection;
import Network.Request;
import Network.Response;

import java.io.IOException;

public class RequestHandler
{
    private static String _username = null;

    private static void PrintSimpleResponse(SimpleResponse response)
    {
        System.out.printf("Code: %d, message: %s\n", response.GetResponse(), response.GetErrorMessage());
    }

    private static void PrintOrderResponse(SimpleResponse response)
    {
        System.out.printf("Code: %d, message: %s\n", response.GetResponse(), response.GetErrorMessage());
    }

    private static Response SendAndWaitResponse(Connection connection, Request request)
    {
        try { connection.Send(request); }
        catch (IOException e) { return null; }

        Response response;
        try { response = connection.ReceiveResponse(); }
        catch (IOException e) { return null; }

        return response;
    }

    /**
     * Handles a registration request from the user.
     *
     * @param connection The connection to the server.
     * @param words An array of strings containing the command and arguments.
     * @return True the connection is still alive, false otherwise.
     */
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

        SimpleResponse response = (SimpleResponse) SendAndWaitResponse(connection, register);
        if (response == null) { return false; }

        PrintSimpleResponse(response);
        return true;
    }

    /**
     * Handles an update credentials request from the user.
     *
     * @param connection The connection to the server.
     * @param words An array of strings containing the command and arguments.
     * @return True the connection is still alive, false otherwise.
     */
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

        SimpleResponse response = (SimpleResponse) SendAndWaitResponse(connection, updateCredentials);
        if (response == null) { return false; }

        PrintSimpleResponse(response);
        return true;
    }

    /**
     * Handles a login request from the user.
     *
     * @param connection The connection to the server.
     * @param words An array of strings containing the command and arguments.
     * @return True the connection is still alive, false otherwise.
     */
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

        SimpleResponse response = (SimpleResponse) SendAndWaitResponse(connection, login);
        if (response == null) { return false; }

        // update the username if login is successful
        if (response.GetResponse() == LoginRequest.OK.GetResponse()) { _username = username; }
        PrintSimpleResponse(response);
        return true;
    }

    /**
     * Handles a logout request from the user.
     *
     * @param connection The connection to the server.
     * @param words An array of strings containing the command and arguments.
     * @return True the connection is still alive, false otherwise.
     */
    public static boolean SendLogout(Connection connection, String[] words)
    {
        if (words.length != 2)
        {
            System.out.println("Usage: logout <username>");
            return true;
        }

        String username = words[1];

        // check if the user is actually logged in
        if (!username.equals(_username))
        {
            SimpleResponse response = LogoutRequest.OTHER_ERROR_CASES;
            PrintSimpleResponse(response);
            return true;
        }

        LogoutRequest logout = new LogoutRequest();
        SimpleResponse response = (SimpleResponse) SendAndWaitResponse(connection, logout);
        if (response == null) { return false; }

        if (response.GetResponse() == LogoutRequest.OK.GetResponse()) { _username = null; }
        PrintSimpleResponse(response);
        return true;
    }
}
