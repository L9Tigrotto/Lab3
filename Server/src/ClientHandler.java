
import Messages.LoginRequest;
import Messages.LogoutRequest;
import Messages.UpdateCredentialsRequest;
import Network.Request;
import Users.DuplicateUserException;
import Users.User;
import Messages.RegisterRequest;
import Network.Connection;

import java.io.EOFException;
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
     * Constructs a new ClientHandler object for the given client socket.
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
            // wait for a request from the client, handle disconnection or server shutdown during the wait
            WaitForRequest();
            if (GlobalData.LISTENER.IsStopRequested()) { return; }

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
                    case "insertLimitOrder" -> HandleInsertLimitOrderRequest(request);
                    case "insertMarketOrder" -> HandleInsertMarketOrderRequest(request);
                    case "insertStopOrder" -> HandleInsertStopOrderRequest(request);
                    case "cancelOrder" -> HandleCancelOrderRequest(request);
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
            catch (EOFException e)
            {
                System.out.println("[Warning] Socket closed");
                try { _connection.Close(); }
                catch (IOException ioe)
                {
                    System.out.println("[Error] Generic error");
                    System.err.println(ioe.getMessage());
                }
                return;
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
                try { _connection.Close(); }
                catch (IOException ioe)
                {
                    System.out.println("[Error] Generic error");
                    System.err.println(ioe.getMessage());
                }
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
                    System.out.println("[Warning] Inactive client detected, closing connection");

                    // if a user is associated with this connection, mark them as disconnected.
                    if (_user != null)
                    {
                        // synchronize access to the user object to prevent race conditions when multiple
                        // requests attempt to modify the same user concurrently.
                        // Note: This synchronization is necessary even if the ClientHandler class is designed
                        // to be run on a single thread, as other parts of the system might interact with the
                        // User object concurrently.
                        // noinspection SynchronizeOnNonFinalField
                        synchronized (_user) { _user.Disconnect(); }
                    }

                    _connection.Close();
                    return;
                }
            }
        }
        catch (InterruptedException ignored) { }
        catch (IOException e)
        {
            System.out.println("[Error] Generic error");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Handles a registration request from the client.
     *
     * This method validates the provided username and password, attempts to create a new user account,
     * and sends an appropriate response to the client based on the outcome of the registration process.
     *
     * @param register The registration request received from the client.
     * @throws IOException If an I/O error occurs while sending the response to the client.
     */
    private void HandleRegisterRequest(RegisterRequest register) throws IOException
    {
        String username = register.GetUsername();
        String password = register.GetPassword();

        try
        {
            // check if the username is valid (meets length requirements, etc.) and if it is already taken by another user
            if (!User.IsUsernameValid(username) || User.Exists(username)) { _connection.Send(RegisterRequest.USERNAME_NOT_AVAILABLE); return; }

            // check if the provided password meets the minimum length and complexity requirements
            if (!User.IsPasswordValid(password)) { _connection.Send(RegisterRequest.INVALID_PASSWORD); return; }

            // attempt to insert the new user into the database. Handle the case where the username is already taken,
            // even though the initial check might have missed it. This could happen due to race conditions
            try { User.Insert(username, password); }
            catch (DuplicateUserException e) { _connection.Send(RegisterRequest.USERNAME_NOT_AVAILABLE); return; }

            // if all checks pass and the user is successfully inserted, send an OK response
            _connection.Send(RegisterRequest.OK);
        }
        catch (IOException e)
        {
            System.out.printf("[Error] %s\n", e.getMessage());
            _connection.Close();
        }
        catch (Exception e)
        {
            System.out.printf("[Error] %s\n", e.getMessage());
            _connection.Send(RegisterRequest.OTHER_ERROR_CASES);
        }
    }

    /**
     * Handles an UpdateCredentialsRequest from the client.
     *
     * This method validates the request, checks the user's credentials, updates the password
     * if valid, and sends an appropriate response to the client.
     *
     * @param request The UpdateCredentialsRequest received from the client.
     * @throws IOException If an I/O error occurs during communication with the client.
     */
    private void HandleUpdateCredentialRequest(UpdateCredentialsRequest request) throws IOException
    {
        String username = request.GetUsername();
        String oldPassword = request.GetOldPassword();
        String newPassword = request.GetNewPassword();

        try
        {
            // check if the new password meets the minimum length and complexity requirements
            if (!User.IsPasswordValid(newPassword)) { _connection.Send(UpdateCredentialsRequest.INVALID_NEWPASSWORD); return; }

            // it checks if a user is already logged in on this specific client connection, not whether there
            // are any connected users in general
            if (_user != null) { _connection.Send(UpdateCredentialsRequest.USER_LOGGED_IN); return; }

            // check if the specified username exists in the system
            if (!User.Exists(username)) { _connection.Send(UpdateCredentialsRequest.NON_EXISTENT_USER); return; }

            // Synchronize access to the user object to prevent race conditions when multiple requests attempt to modify
            // the same user concurrently
            User user = User.FromName(username);
            synchronized (user)
            {
                // verify that the provided old password matches the user's current password
                if (!user.MatchPassword(oldPassword)) { _connection.Send(UpdateCredentialsRequest.USERNAME_OLDPASSWORD_MISMATCH); return; }

                // prevent the user from setting the new password to the same as the old password
                if (user.MatchPassword(newPassword)) { _connection.Send(UpdateCredentialsRequest.NEW_AND_OLD_PASSWORD_EQUAL); return; }

                // specifically checks if the target user is connected to another session, not just whether any user is
                // connected to this particular ClientHandler instance
                if (user.IsConnected()) { _connection.Send(UpdateCredentialsRequest.USER_LOGGED_IN); return; }

                // update the user's password with the new password
                user.ChangePassword(newPassword);
            }

            // send an OK response to the client, indicating successful password update
            _connection.Send(UpdateCredentialsRequest.OK);
        }
        catch (IOException e)
        {
            System.out.printf("[Error] %s\n", e.getMessage());
            _connection.Close();
        }
        catch (Exception e)
        {
            System.out.printf("[Error] %s\n", e.getMessage());
            _connection.Send(UpdateCredentialsRequest.OTHER_ERROR_CASES);
        }
    }

    /**
     * Handles a LoginRequest from the client.
     *
     * This method validates the user's credentials, marks the user as connected,
     * and sends an appropriate response to the client.
     *
     * @param request The LoginRequest received from the client.
     * @throws IOException If an I/O error occurs during communication with the client.
     */
    private void HandleLoginRequest(LoginRequest request) throws IOException
    {
        String username = request.GetUsername();
        String password = request.GetPassword();

        try
        {
            // checks if the provided username exists
            if (!User.Exists(username))  { _connection.Send(LoginRequest.NON_EXISTENT_USER); return; }

            // synchronize this block to prevent race conditions when multiple requests attempt to modify the
            // same user concurrently
            User user = User.FromName(username);
            synchronized (user)
            {
                // verify that the provided password matches the user's stored password
                if (!user.MatchPassword(password)) { _connection.Send(LoginRequest.USERNAME_PASSWORD_MISMATCH); return; }

                // check if the user is already logged in from another session
                if (user.IsConnected()) { _connection.Send(LoginRequest.USER_ALREADY_LOGGED_IN); return; }

                // mark the user as connected and associate the user object with this client handler
                user.Connect();
                _user = user;
            }

            // send an OK response to the client, indicating successful login
            _connection.Send(LoginRequest.OK);
        }
        catch (IOException e)
        {
            System.out.printf("[Error] %s\n", e.getMessage());
            _connection.Close();
        }
        catch (Exception e)
        {
            System.out.printf("[Error] %s\n", e.getMessage());
            _connection.Send(LoginRequest.OTHER_ERROR_CASES);
        }
    }

    /**
     * Handles a LogoutRequest from the client.
     *
     * This method marks the user as disconnected and sends an appropriate response to the client.
     *
     * @param request The LogoutRequest received from the client.
     * @throws IOException If an I/O error occurs during communication with the client.
     */
    private void HandleLogoutRequest(LogoutRequest request) throws IOException
    {
        try
        {
            // check if the user is currently logged in
            if (_user == null) { _connection.Send(LogoutRequest.USER_NOT_LOGGED); return; }

            // synchronize access to the user object to prevent race conditions when multiple requests attempt
            // to modify the same user concurrently.
            // Note: This synchronization is necessary even if the ClientHandler class is designed to be run
            // on a single thread, as other parts of the system might interact with the User object concurrently.
            // noinspection SynchronizeOnNonFinalField
            synchronized (_user)
            {
                // mark the user as disconnected and disassociate the user object from this client handler
                _user.Disconnect();
                _user = null;
            }

            _connection.Send(LogoutRequest.OK);
        }
        catch (IOException e)
        {
            System.out.printf("[Error] %s\n", e.getMessage());
            _connection.Close();
        }
        catch (Exception e)
        {
            System.out.printf("[Error] %s\n", e.getMessage());
            _connection.Send(LogoutRequest.OTHER_ERROR_CASES);
        }
    }

    private void HandleInsertLimitOrderRequest(Request request) throws IOException
    {

    }

    private void HandleInsertMarketOrderRequest(Request request) throws IOException
    {

    }

    private void HandleInsertStopOrderRequest(Request request) throws IOException
    {

    }

    private void HandleCancelOrderRequest(Request request) throws IOException
    {

    }

    private void HandleGetPriceHistoryRequest(Request request) throws IOException
    {

    }
}
