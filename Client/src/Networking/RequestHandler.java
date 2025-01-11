
package Networking;

import Messages.*;
import Orders.Type;
import Users.User;

import java.io.IOException;

public class RequestHandler
{
    // stores the currently logged-in user
    private static User _user = null;

    /**
     * Prints a simple response to the console.
     *
     * @param response The SimpleResponse object to print.
     */
    private static void PrintSimpleResponse(SimpleResponse response)
    {
        System.out.printf("[INFO] Response received.\n\t-> Code: %d\n\t-> Message: %s\n", response.GetResponse(), response.GetErrorMessage());
    }

    /**
     * Prints an order response to the console.
     *
     * @param response The OrderResponse object to print.
     */
    private static void PrintOrderResponse(OrderResponse response)
    {
        System.out.printf("[INFO] Response received.\n\t-> OrderID: %d", response.GetOrderID());
    }

    /**
     * Sends a request to the server and waits for a response.
     *
     * @param connection The connection to the server.
     * @param request The request to send.
     * @return The response received from the server, or null if an error occurred.
     */
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
     * @return True if the connection is still alive, false otherwise.
     */
    public static boolean SendRegister(Connection connection, String[] words)
    {
        // check for correct number of arguments for register command
        if (words.length != 3)
        {
            System.out.println("[INFO] Usage: register <username> <password>");
            return true;
        }

        // check if a user is already logged in, prevent multiple registrations
        if (_user != null)
        {
            System.out.println("[WARNING] It's not possible to send a registration request if you are already logged in");
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
        // check for correct number of arguments for updateCredentials command
        if (words.length != 4)
        {
            System.out.println("[INFO] Usage: updateCredentials <username> <oldPassword> <newPassword>");
            return true;
        }

        // check if a user is logged in to update credentials
        if (_user != null)
        {
            System.out.println("[WARNING] It's not possible to send an update credential request if you are already logged in");
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
        // check for correct number of arguments for login command
        if (words.length != 3)
        {
            System.out.println("[INFO] Usage: login <username> <password>");
            return true;
        }

        // check if a user is already logged in, prevent multiple logins
        if (_user != null)
        {
            System.out.println("[WARNING] It's not possible to send a login request if you are already logged in");
            return true;
        }

        String username = words[1];
        String password = words[2];
        LoginRequest login = new LoginRequest(username, password);

        SimpleResponse response = (SimpleResponse) SendAndWaitResponse(connection, login);
        if (response == null) { return false; }

        // update the current user if login is successful
        if (response.GetResponse() == LoginRequest.OK.GetResponse()) { _user = new User(username, password); }
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
        // check for correct number of arguments for logout command
        if (words.length != 2)
        {
            System.out.println("[INFO] Usage: logout <username>");
            return true;
        }

        // check if a user is logged in before attempting logout
        if (_user == null)
        {
            System.out.println("[WARNING] It's not possible to send a logout request if you are not logged in");
            return true;
        }

        String username = words[1];

        // check if the provided username matches the logged-in user
        if (!username.equals(_user.GetUsername()))
        {
            System.out.println("[ERROR] username does not match");
            return true;
        }

        LogoutRequest logout = new LogoutRequest();
        SimpleResponse response = (SimpleResponse) SendAndWaitResponse(connection, logout);
        if (response == null) { return false; }

        if (response.GetResponse() == LogoutRequest.OK.GetResponse()) { _user = null; }
        PrintSimpleResponse(response);
        return true;
    }

    /**
     * Handles an insert market order request from the user.
     *
     * @param connection The connection to the server.
     * @param words An array of strings containing the command and arguments.
     * @return True if the connection is still alive, false otherwise.
     */
    public static boolean SendInsertMarketOrder(Connection connection, String[] words)
    {
        // check for correct number of arguments for insertMarketOrder command
        if (words.length != 3)
        {
            System.out.println("[INFO] Usage: insertMarketOrder <type> <size>");
            return true;
        }

        // check if a user is logged in to place orders
        if (_user == null)
        {
            System.out.println("[WARNING] It's not possible to send an insert market order request if you are not logged in");
            return true;
        }

        // validate the order type (bid or ask)
        Type type = Type.FromString(words[1]);
        if (type == null)
        {
            System.out.println("[ERROR] <type> must be one of: bid, ask");
            return true;
        }

        // validate the order size (number)
        long size;

        try { size = Long.parseLong(words[2]); }
        catch (NumberFormatException e) { System.out.println("[ERROR] <size> is not a number"); return true; }

        // TODO
        return true;
    }

    /**
     * Handles an insert limit order request from the user.
     *
     * @param connection The connection to the server.
     * @param words An array of strings containing the command and arguments.
     * @return True if the connection is still alive, false otherwise.
     */
    public static boolean SendInsertLimitOrder(Connection connection, String[] words)
    {
        // check for correct number of arguments for insertLimitOrder command
        if (words.length != 4)
        {
            System.out.println("[INFO] Usage: insertLimitOrder <type> <size> <limit>");
            return true;
        }

        // check if a user is logged in to place orders
        if (_user == null)
        {
            System.out.println("[WARNING] It's not possible to send an insert limit order request if you are not logged in");
            return true;
        }

        // validate the order type (bid or ask)
        Type type = Type.FromString(words[1]);
        if (type == null)
        {
            System.out.println("[ERROR] <type> must be one of: bid, ask");
            return true;
        }

        // validate the order size (number) and the order limit (number)
        long size, limit;

        try { size = Long.parseLong(words[2]); }
        catch (NumberFormatException e) { System.out.println("[ERROR] <size> is not a number"); return true; }

        try { limit = Long.parseLong(words[3]); }
        catch (NumberFormatException e)
        {
            System.out.println("[ERROR] <limit> is not a number");
            return true;
        }

        LimitOrderRequest request = new LimitOrderRequest(type, size, limit);
        OrderResponse response = (OrderResponse) SendAndWaitResponse(connection, request);
        if (response == null) { return false; }

        PrintOrderResponse(response);
        return true;
    }

    /**
     * Handles an insert stop order request from the user.
     *
     * @param connection The connection to the server.
     * @param words An array of strings containing the command and arguments.
     * @return True if the connection is still alive, false otherwise.
     */
    public static boolean SendInsertStopOrder(Connection connection, String[] words)
    {
        // check for correct number of arguments for insertStopOrder command
        if (words.length != 4)
        {
            System.out.println("[INFO] Usage: insertStopOrder <type> <size> <stopPrice>");
            return true;
        }

        // check if a user is logged in to place orders
        if (_user == null)
        {
            System.out.println("[WARNING] It's not possible to send an insert stop order request if you are not logged in");
            return true;
        }

        // validate the order type (bid or ask)
        Type type = Type.FromString(words[1]);
        if (type == null)
        {
            System.out.println("[ERROR] <type> must be one of: bid, ask");
            return true;
        }

        // validate the order size (number) and the order stopPrice (number)
        long size, stopPrice;

        try { size = Long.parseLong(words[2]); }
        catch (NumberFormatException e) { System.out.println("[ERROR] <size> is not a number"); return true; }

        try { stopPrice = Long.parseLong(words[3]); }
        catch (NumberFormatException e) { System.out.println("[ERROR] <stopPrice> is not a number"); return true; }

        // TODO
        return true;
    }

    /**
     * Handles a cancel order request from the user.
     *
     * @param connection The connection to the server.
     * @param words An array of strings containing the command and arguments.
     * @return True if the connection is still alive, false otherwise.
     */
    public static boolean SendCancelOrder(Connection connection, String[] words)
    {
        // check for correct number of arguments for cancelOrder command
        if (words.length != 2)
        {
            System.out.println("[INFO] Usage: cancelOrder <orderID>");
            return true;
        }

        // check if a user is logged in to place orders
        if (_user == null)
        {
            System.out.println("[WARNING] It's not possible to send an insert limit order request if you are not logged in");
            return true;
        }

        // validate the order id (number)
        long orderID;

        try { orderID = Long.parseLong(words[1]); }
        catch (NumberFormatException e) { System.out.println("[ERROR] <size> is not a number"); return true; }

        // TODO
        return true;
    }
}
