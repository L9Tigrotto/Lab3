package Networking;

import Helpers.GlobalData;
import Messages.*;
import Users.User;
import Users.UserNotRegisteredException;

import java.io.IOException;
import java.net.Socket;

/**
 * This class represents a handler for a connected client. It is responsible for receiving requests from the client,
 * processing them, and sending responses back. The handler also monitors for client inactivity and disconnects idle clients.
 */
public class ClientHandler implements Runnable
{
    // the connection object for communication with the client
    private final Connection _connection;

    // tracks the timestamp of the last received message from the client
    private long _lastMessageTime;

    // the User object associated with this client, if authenticated
    private User _user;

    /**
     * Constructs a new Networking.ClientHandler object for the given client socket.
     *
     * @param socket The socket representing the connected client.
     * @throws IOException If an I/O error occurs during connection setup.
     */
    public ClientHandler(Socket socket) throws IOException
    {
        _connection = new Connection(socket);
        _lastMessageTime = System.currentTimeMillis();
        _user = null;
    }

    /**
     * The main loop of the client handler thread. It continuously waits for requests from the client, processes them,
     * and sends responses. The loop exits when the server is shutting down or the client disconnects.
     */
    @Override
    public void run()
    {
        while (true)
        {
            // wait for a request from the client, handle disconnection due inactivity or server shutdown during the wait
            WaitForRequest();
            if (_connection.IsClosed() || GlobalData.LISTENER.IsStopRequested()) { return; }

            try
            {
                // receive the request from the client
                Request request = _connection.ReceiveRequest();

                // handle the request based on its operation type
                switch (request.GetOperation())
                {
                    case "register" -> HandleRegisterRequest((RegisterRequest) request);
                    case "updateCredentials" -> HandleUpdateCredentialRequest((UpdateCredentialsRequest) request);
                    case "login" -> HandleLoginRequest((LoginRequest) request);
                    case "logout" -> HandleLogoutRequest((LogoutRequest) request);
                    case "insertMarketOrder" -> HandleInsertMarketOrderRequest((MarketOrderRequest) request);
                    case "insertLimitOrder" -> HandleInsertLimitOrderRequest((LimitOrderRequest) request);
                    case "insertStopOrder" -> HandleInsertStopOrderRequest((StopOrderRequest) request);
                    case "cancelOrder" -> HandleCancelOrderRequest((CancelOrderRequest) request);
                    case "getPriceHistory" -> HandleGetPriceHistoryRequest(request);
                    default ->
                    {
                        System.out.printf("[Info] Received unknown request message %s, ignoring\n", request.GetOperation());
                        continue;
                    }
                }

                // update the last message time to track client activity
                _lastMessageTime = System.currentTimeMillis();
            }
            catch (IOException e)
            {
                System.out.println("[ERROR] Unable to close connection, interrupting communications with the client");
                return;
            }
        }
    }

    /**
     * Waits for data to be available on the client socket, with a timeout mechanism. If no data is received within
     * the timeout period and the client has been inactive for longer than the configured threshold, the connection
     * is considered inactive and closed.
     */
    private void WaitForRequest()
    {
        try
        {
            while(!GlobalData.LISTENER.IsStopRequested() && !_connection.IsDataAvailable())
            {
                Thread.sleep(GlobalData.SETTINGS.WaitDataTimeoutMS);

                // calculate the elapsed time since the last message was received and check if it exceeds the
                // inactivity threshold
                long elapsedTime = System.currentTimeMillis() - _lastMessageTime;
                boolean terminate = elapsedTime > GlobalData.SETTINGS.ClientInactiveThresholdMS;

                if (terminate)
                {
                    System.out.println("[WARNING] Inactive client detected, closing connection");

                    // if a user is associated with this connection, mark them as disconnected.
                    if (_user != null) { _user.TryLogout(); }

                    _connection.Close();
                    return;
                }
            }
        }
        catch (InterruptedException ignored) { }
        catch (IOException e) { System.out.printf("[ERROR] Checking if any data is available: %s\n", e.getMessage()); }
    }

    private void SendResponse(Response response) throws IOException
    {
        try { _connection.Send(response); }
        catch (IOException e)
        {
            System.out.printf("[ERROR] Unable to send response: %s\n", e.getMessage());
            _connection.Close();
        }
    }

    /**
     * Handles a registration request from the client.
     *
     * This method validates the provided username and password, attempts to create a new user account,
     * and sends an appropriate response to the client based on the outcome of the registration process.
     *
     * @param register The registration request received from the client.
     * @throws IOException If an I/O error occurs while closing the connection due to an error.
     */
    private void HandleRegisterRequest(RegisterRequest register) throws IOException
    {
        String username = register.GetUsername();
        String password = register.GetPassword();
        SimpleResponse response;

        // check if the username is valid (meets length requirements, etc.) and if it is already taken by another user
        if (!User.IsUsernameValid(username) || GlobalData.UserExists(username)) { response = RegisterRequest.USERNAME_NOT_AVAILABLE; }

        // check if the provided password meets the minimum length and complexity requirements
        else if (!User.IsPasswordValid(password)) { response = RegisterRequest.INVALID_PASSWORD; }

        // attempt to register the user and get the response
        else { response = GlobalData.TryRegisterUser(username, password); }

        SendResponse(response);
    }

    /**
     * Handles an UpdateCredentialsRequest from the client.
     *
     * This method validates the request, checks the user's credentials, updates the password
     * if valid, and sends an appropriate response to the client.
     *
     * @param request The UpdateCredentialsRequest received from the client.
     * @throws IOException If an I/O error occurs while closing the connection due to an error.
     */
    private void HandleUpdateCredentialRequest(UpdateCredentialsRequest request) throws IOException
    {
        String username = request.GetUsername();
        String oldPassword = request.GetOldPassword();
        String newPassword = request.GetNewPassword();
        SimpleResponse response;

        // check if the user is already logged in on this connection
        if (_user != null) { response = UpdateCredentialsRequest.USER_LOGGED_IN; }

        // check if the new password meets the minimum length and complexity requirements
        else if (!User.IsPasswordValid(newPassword)) { response = UpdateCredentialsRequest.INVALID_NEWPASSWORD; }

        // check if the specified username exists in the system
        else if (!GlobalData.UserExists(username)) { response = UpdateCredentialsRequest.NON_EXISTENT_USER; }

        // attempt to update the user's password
        else
        {
            try
            {
                User user = GlobalData.UserFromName(username);
                response = user.TryUpdatePassword(oldPassword, newPassword);
            } catch (UserNotRegisteredException e) { response = UpdateCredentialsRequest.NON_EXISTENT_USER; }
        }

        SendResponse(response);
    }

    /**
     * Handles a LoginRequest from the client.
     *
     * This method validates the user's credentials, marks the user as connected,
     * and sends an appropriate response to the client.
     *
     * @param request The LoginRequest received from the client.
     * @throws IOException If an I/O error occurs while closing the connection due to an error.
     */
    private void HandleLoginRequest(LoginRequest request) throws IOException
    {
        String username = request.GetUsername();
        String password = request.GetPassword();
        SimpleResponse response;

        // check if the user is already logged in on this connection
        if (_user != null) { response = LoginRequest.USER_ALREADY_LOGGED_IN; }

        // check if the specified username exists in the system
        else if (!GlobalData.UserExists(username)) { response = LoginRequest.NON_EXISTENT_USER; }

        // attempt to log the user in
        else
        {
            try
            {
                User user = GlobalData.UserFromName(username);
                response = user.TryLogIn(password);

                // associate the user object with this client handler if login is successful
                if (response.GetResponse() == LoginRequest.OK.GetResponse()) { _user = user; }
            } catch (UserNotRegisteredException e) { response = LoginRequest.NON_EXISTENT_USER; }
        }


        SendResponse(response);
    }

    /**
     * Handles a LogoutRequest from the client.
     *
     * This method marks the user as disconnected and sends an appropriate response to the client.
     *
     * @param request The LogoutRequest received from the client.
     * @throws IOException If an I/O error occurs while closing the connection due to an error.
     */
    private void HandleLogoutRequest(LogoutRequest request) throws IOException
    {
        SimpleResponse response;

        // check if the user is currently logged in
        if (_user == null) { response = LogoutRequest.USER_NOT_LOGGED; }

        else
        {
            response = _user.TryLogout();

            // clear the user reference if logout was successful
            if (response.GetResponse() == LoginRequest.OK.GetResponse()) { _user = null; }
        }

        SendResponse(response);
    }

    private void HandleInsertMarketOrderRequest(MarketOrderRequest request) throws IOException
    {

    }

    private void HandleInsertLimitOrderRequest(LimitOrderRequest request) throws IOException
    {

    }

    private void HandleInsertStopOrderRequest(StopOrderRequest request) throws IOException
    {

    }

    private void HandleCancelOrderRequest(CancelOrderRequest request) throws IOException
    {

    }

    private void HandleGetPriceHistoryRequest(Request request) throws IOException
    {

    }
}
