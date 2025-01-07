
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
                    case "login" -> HandleLoginRequest(request);
                    case "logout" -> HandleLoginRequest(request);
                    case "insertLimitOrder" -> HandleLogoutRequest(request);
                    case "insertMarketOrder" -> HandleInsertLimitOrderRequest(request);
                    case "insertStopOrder" -> HandleInsertMarketOrderRequest(request);
                    case "cancelOrder" -> HandleInsertStopOrderRequest(request);
                    case "getPriceHistory" -> HandleCancelOrderRequest(request);
                    case "closedTrades" -> HandleGetPriceHistoryRequest(request);
                    default ->
                    {
                        System.out.printf("[Info] Received unknown request message %s, ignoring\n", request.GetOperation());
                        continue;
                    }
                }

                // Update the last message time to track client activity
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
                boolean terminate = _lastMessageTime + GlobalData.SETTINGS.ClientInactiveThresholdMS < System.currentTimeMillis();
                if (terminate)
                {
                    System.out.println("[Warning] Inactive client detected, closing connection");
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
        try
        {
            String username = register.GetUsername();
            String password = register.GetPassword();

            if (!User.IsUsernameValid(username) || User.Exists(username))
            {
                _connection.Send(RegisterRequest.USERNAME_NOT_AVAILABLE);
                return;
            }

            if (!User.IsPasswordValid(password))
            {
                _connection.Send(RegisterRequest.INVALID_PASSWORD);
                return;
            }

            try { User.Insert(username, password); }
            catch (DuplicateUserException e)
            {
                _connection.Send(RegisterRequest.USERNAME_NOT_AVAILABLE);
                return;
            }

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

    private void HandleUpdateCredentialRequest(UpdateCredentialsRequest request) throws IOException
    {
        try
        {
            String username = request.GetUsername();
            String oldPassword = request.GetOldPassword();
            String newPassword = request.GetNewPassword();

            if (!User.Exists(username))
            {
                _connection.Send(UpdateCredentialsRequest.NON_EXISTENT_USER);
                return;
            }

            if (!User.IsPasswordValid(newPassword))
            {
                _connection.Send(UpdateCredentialsRequest.INVALID_NEWPASSWORD);
                return;
            }

            // synchronize this block to avoid concurrent requests to check credentials correctly
            User user = User.FromName(username);
            synchronized (user)
            {
                if (!user.ArePasswordEquals(oldPassword))
                {
                    _connection.Send(UpdateCredentialsRequest.USERNAME_OLDPASSWORD_MISMATCH);
                    return;
                }

                if (user.ArePasswordEquals(newPassword))
                {
                    _connection.Send(UpdateCredentialsRequest.NEW_AND_OLD_PASSWORD_EQUAL);
                    return;
                }

                if (user.IsConnected())
                {
                    _connection.Send(UpdateCredentialsRequest.USER_LOGGED_IN);
                    return;
                }

                user.ChangePassword(newPassword);
            }

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

    private void HandleLoginRequest(Request request)
    {

    }

    private void HandleLogoutRequest(Request request)
    {

    }

    private void HandleInsertLimitOrderRequest(Request request)
    {

    }

    private void HandleInsertMarketOrderRequest(Request request)
    {

    }

    private void HandleInsertStopOrderRequest(Request request)
    {

    }

    private void HandleCancelOrderRequest(Request request)
    {

    }

    private void HandleGetPriceHistoryRequest(Request request)
    {

    }
}
