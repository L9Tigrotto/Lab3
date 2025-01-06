
import Network.Request;
import Users.DuplicateUserException;
import Users.User;
import Messages.RegisterRequest;
import Network.Connection;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

public class ClientHandler implements Runnable
{
    private final Connection _connection;
    private final AtomicLong _lastMessageTime;

    public ClientHandler(Socket socket) throws IOException
    {
        _connection = new Connection(socket);
        _lastMessageTime = new AtomicLong(System.currentTimeMillis());
    }

    public long GetLastMessageTime() { return _lastMessageTime.get(); }

    @Override
    public void run()
    {
        while (true)
        {
            WaitForRequest();
            if (GlobalData.LISTENER.IsStopRequested()) { return; }

            try
            {
                Request request = _connection.ReceiveRequest();

                // filter the request type and begin working on the response
                switch (request.GetOperation())
                {
                    case "register" -> HandleRegisterRequest((RegisterRequest) request);
                    case "login" -> HandleUpdateCredentialRequest(request);
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

                // update the last message time to ensure the full inactivity period elapses
                _lastMessageTime.set(System.currentTimeMillis());
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

    private void WaitForRequest()
    {
        try
        {
            while(!GlobalData.LISTENER.IsStopRequested() && !_connection.IsDataAvailable())
            {
                Thread.sleep(GlobalData.SETTINGS.ReadTimeoutMS);
                boolean terminate = _lastMessageTime.get() + GlobalData.SETTINGS.InactiveTerminationMS < System.currentTimeMillis();
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

    private void HandleRegisterRequest(RegisterRequest register) throws IOException
    {
        try
        {
            String username = register.GetUsername();
            String password = register.GetPassword();

            if (!register.IsPasswordValid())
            {
                _connection.Send(RegisterRequest.INVALID_PASSWORD_RESPONSE);
                return;
            }

            if (!register.IsUsernameValid() && !User.Exists(username))
            {
                _connection.Send(RegisterRequest.USERNAME_NOT_AVAILABLE_RESPONSE);
                return;
            }

            try { User.Insert(username, password); }
            catch (DuplicateUserException e)
            {
                _connection.Send(RegisterRequest.USERNAME_NOT_AVAILABLE_RESPONSE);
                return;
            }

            _connection.Send(RegisterRequest.OK_RESPONSE);
        }
        catch (IOException e)
        {
            System.out.println("[Error] Unable to send response message");
            System.err.println(e.getMessage());
            _connection.Close();
        }
        catch (Exception e)
        {
            System.out.println("[Error] Generic error");
            System.err.println(e.getMessage());
            _connection.Send(RegisterRequest.OTHER_ERROR_CASES_RESPONSE);
        }
    }

    private void HandleUpdateCredentialRequest(Request request) throws IOException
    {

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
