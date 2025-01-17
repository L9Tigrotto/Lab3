
package Networking;

import Helpers.GlobalData;
import Helpers.Tuple;
import Messages.*;
import Orders.*;
import Users.User;
import Users.UserCollection;
import Users.UserNotRegisteredException;

import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;
import java.util.List;

/**
 * This class represents a handler for a connected client. It is responsible for receiving requests from the client,
 * processing them, and sending responses back. The handler also monitors for client inactivity and disconnects idle clients.
 */
public class ClientHandler implements Runnable
{
    // the connection object for communication with the client (handles both TCP and UDP)
    private final Connection _connection;

    // tracks the timestamp of the last received message from the client for inactivity detection
    private long _lastMessageTime;

    // the User object associated with this client, if authenticated.
    private User _user;

    /**
     * Constructs a new Networking.ClientHandler object for the given client socket.
     *
     * @param socket The socket representing the connected client.
     * @throws IOException If an I/O error occurs during connection setup.
     */
    public ClientHandler(Socket socket) throws IOException
    {
        _connection = new Connection(socket, GlobalData.SOCKET_UDP, GlobalData.SETTINGS.MULTICAST_IP, GlobalData.SETTINGS.MULTICAST_PORT);
        _lastMessageTime = System.currentTimeMillis();
        _user = null;  // initially no user is logged in
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
            if (_connection.IsClosed() || GlobalData.TCP_LISTENER.IsStopRequested()) { return; }

            try
            {
                // receive the request from the client
                Request request;
                try { request = _connection.ReceiveRequest(); }
                catch (ParseException e)
                {
                    System.out.println("[ERROR] Unable to parse the request, interrupting communications with the client");
                    _connection.Close(); return;
                }

                // handle the request based on its operation type
                switch (request.GetOperation()){
                    case REGISTER -> HandleRegisterRequest((RegisterRequest) request);
                    case UPDATE_CREDENTIALS -> HandleUpdateCredentialRequest((UpdateCredentialsRequest) request);
                    case LOGIN -> HandleLoginRequest((LoginRequest) request);
                    case LOGOUT -> HandleLogoutRequest((LogoutRequest) request);
                    case INSERT_MARKET_ORDER -> HandleInsertMarketOrderRequest((MarketOrderRequest) request);
                    case INSERT_LIMIT_ORDER -> HandleInsertLimitOrderRequest((LimitOrderRequest) request);
                    case INSERT_STOP_ORDER -> HandleInsertStopOrderRequest((StopOrderRequest) request);
                    case CANCEL_ORDER -> HandleCancelOrderRequest((CancelOrderRequest) request);
                    case GET_PRICE_HISTORY -> HandleGetPriceHistoryRequest((GetPriceHistoryRequest) request);
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
            while(!GlobalData.TCP_LISTENER.IsStopRequested() && !_connection.IsDataAvailable())
            {
                // calculate the elapsed time since the last message was received and check if
                // it exceeds the inactivity threshold
                long elapsedTime = System.currentTimeMillis() - _lastMessageTime;
                boolean terminate = elapsedTime > GlobalData.SETTINGS.ClientInactiveThresholdMS;

                if (terminate)
                {
                    System.out.println("[WARNING] Inactive client detected, closing connection");

                    // if a user is associated with this connection, mark them as disconnected
                    if (_user != null) { UserCollection.TryLogout(_user); }

                    _connection.Close();
                    return;
                }

                // sleep and check again
                Thread.sleep(GlobalData.SETTINGS.WaitDataTimeoutMS);
            }
        }
        catch (InterruptedException ignored) { }
        catch (IOException e) { System.out.printf("[ERROR] Checking if any data is available: %s\n", e.getMessage()); }
    }

    /**
     * Sends the provided response back to the client.
     *
     * @param response The response object to be sent to the client.
     * @throws IOException If an I/O error occurs while sending the response.
     */
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

        // check if the username is valid and available
        if (!User.IsUsernameValid(username) || UserCollection.IsRegistered(username)) { SendResponse(RegisterRequest.USERNAME_NOT_AVAILABLE); }
        else if (!User.IsPasswordValid(password)) { SendResponse(RegisterRequest.INVALID_PASSWORD); }
        else
        {
            SimpleResponse response = UserCollection.TryRegister(username, password);
            SendResponse(response);
        }
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

        // check if the user is logged in and if the new password is valid.
        if (_user != null) { SendResponse(UpdateCredentialsRequest.USER_LOGGED_IN); }
        else if (!User.IsPasswordValid(newPassword)) { SendResponse(UpdateCredentialsRequest.INVALID_NEWPASSWORD); }
        else if (!UserCollection.IsRegistered(username)) { SendResponse(UpdateCredentialsRequest.NON_EXISTENT_USER); }

        // attempt to update the user's password
        else
        {
            try
            {
                User user = UserCollection.FromName(username);
                SimpleResponse response = user.TryUpdatePassword(oldPassword, newPassword);
                SendResponse(response);
            } catch (UserNotRegisteredException e) { SendResponse(UpdateCredentialsRequest.NON_EXISTENT_USER); }
        }
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

        // check if the user is already logged in
        if (_user != null) { SendResponse(LoginRequest.USER_ALREADY_LOGGED_IN); }
        else if (!UserCollection.IsRegistered(username)) { SendResponse(LoginRequest.NON_EXISTENT_USER); }
        else
        {
            User user = null;
            try { user = UserCollection.FromName(username); }
            catch (UserNotRegisteredException e) { SendResponse(LoginRequest.OTHER_ERROR_CASES); return; } // should not happen

            SimpleResponse response = UserCollection.TryLogin(user, password, _connection);

            // if login is successful, associate the user with the handler.
            if (response.GetResponse() == LoginRequest.OK.GetResponse()) { _user = user; }
        }
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
        if (_user == null) { SendResponse(LogoutRequest.USER_NOT_LOGGED); }

        else
        {
            response = UserCollection.TryLogout(_user);
            SendResponse(response);
            // clear the user reference if logout was successful
            if (response.GetResponse() == LogoutRequest.OK.GetResponse()) { _user = null; }
        }
    }

    /**
     * Handles an InsertMarketOrderRequest from the client.
     * Attempts to place a market order and sends a response.
     */
    private void HandleInsertMarketOrderRequest(MarketOrderRequest request) throws IOException
    {
        Tuple<OrderResponse, String> response_message;

        // check if the user is currently logged in
        if (_user == null) { SendResponse(OrderResponse.INVALID); }

        else
        {
            MarketOrder order = GlobalData.CreateMarketOrder(request, _user);
            response_message = OrderBook.ProcessOrder(order);
            SendResponse(response_message.GetX());

            // send any notifications related to the order
            if (!response_message.GetY().isEmpty()) { _connection.SendNotification(response_message.GetY()); }
        }
    }

    /**
     * Handles an InsertLimitOrderRequest from the client.
     * Attempts to place a limit order and sends a response.
     */
    private void HandleInsertLimitOrderRequest(LimitOrderRequest request) throws IOException
    {
        Tuple<OrderResponse, List<String>> response_message;

        // check if the user is currently logged in
        if (_user == null) { SendResponse(OrderResponse.INVALID); }

        else
        {
            LimitOrder order = GlobalData.CreateLimitOrder(request, _user);
            response_message = OrderBook.ProcessOrder(order);

            SendResponse(response_message.GetX());

            // send any notifications related to the order
            for (String message : response_message.GetY())
            {
                if (!response_message.GetY().isEmpty()) { _connection.SendNotification(message); }
            }
        }
    }

    /**
     * Handles an InsertStopOrderRequest from the client.
     * Attempts to place a stop order and sends a response.
     */
    private void HandleInsertStopOrderRequest(StopOrderRequest request) throws IOException
    {
        Tuple<OrderResponse, String> response_message;

        // check if the user is currently logged in
        if (_user == null) { SendResponse(OrderResponse.INVALID); }

        else
        {
            StopOrder order = GlobalData.CreateStopOrder(request, _user);
            response_message = OrderBook.ProcessOrder(order);
            SendResponse(response_message.GetX());

            // send any notifications related to the order
            if (!response_message.GetY().isEmpty()) { _connection.SendNotification(response_message.GetY()); }
        }
    }

    /**
     * Handles a CancelOrderRequest from the client.
     * Attempts to cancel an existing order and sends a response.
     */
    private void HandleCancelOrderRequest(CancelOrderRequest request) throws IOException
    {
        // check if the user is currently logged in
        if (_user == null) { SendResponse(CancelOrderRequest.OTHER_ERROR_CASES); }
        else
        {
            SimpleResponse response = OrderBook.TryCancelOrder(request, _user);
            SendResponse(response);
        }
    }

    /**
     * Handles a GetPriceHistoryRequest from the client.
     * Sends the price history for the requested asset.
     */
    private void HandleGetPriceHistoryRequest(GetPriceHistoryRequest request) throws IOException
    {
        // check if the user is currently logged in
        if (_user == null) { SendResponse(GetPriceHistoryRequest.USER_NOT_LOGGED); }
        else
        {
            SimpleResponse response = HistoryRecordCollection.GetPrices(request);
            SendResponse(response);
        }
    }
}
