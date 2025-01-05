
import Users.DuplicateUserException;
import Users.User;
import Network.Message;
import Messages.Registration;
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
            WaitForData();
            if (!_connection.IsAlive()) { return; }

            try
            {
                Message message = _connection.Receive();

                // client is not meant to receive response messages
                if (message.GetType().IsResponse())
                {
                    System.out.printf("[Info] Received response message {%s}, ignoring\n", message.GetType());
                    continue;
                }

                // filter the request type and begin working on the response
                switch (message.GetType())
                {
                    case RegisterRequest -> HandleRegisterRequest(message);
                    case UpdateCredentialsRequest -> HandleUpdateCredentialRequest(message);
                    case LoginRequest -> HandleLoginRequest(message);
                    case LogoutRequest -> HandleLogoutRequest(message);
                    case InsertLimitOrderRequest -> HandleInsertLimitOrderRequest(message);
                    case InsertMarketOrderRequest -> HandleInsertMarketOrderRequest(message);
                    case InsertStopOrderRequest -> HandleInsertStopOrderRequest(message);
                    case CancelOrderRequest -> HandleCancelOrderRequest(message);
                    case GetPriceHistoryRequest -> HandleGetPriceHistoryRequest(message);
                    default ->
                    {
                        System.out.printf("[Info] Received unknown response message %s, ignoring\n", message.GetType());
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

    private void WaitForData()
    {
        try
        {
            while(!_connection.IsDataAvailable())
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

    private void HandleRegisterRequest(Message message) throws IOException
    {
        try
        {
            Registration registerRequest = Registration.FromMessage(message);
            String username = registerRequest.GetUsername();
            String password = registerRequest.GetPassword();

            if (!registerRequest.IsPasswordValid())
            {
                _connection.Send(Registration.INVALID_PASSWORD_RESPONSE);
                return;
            }

            if (!registerRequest.IsUsernameValid() && !User.Exists(username))
            {
                _connection.Send(Registration.USERNAME_NOT_AVAILABLE_RESPONSE);
                return;
            }

            try { User.Insert(username, password); }
            catch (DuplicateUserException e)
            {
                _connection.Send(Registration.USERNAME_NOT_AVAILABLE_RESPONSE);
                return;
            }

            _connection.Send(Registration.OK_RESPONSE);
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
            _connection.Send(Registration.OTHER_ERROR_CASES_RESPONSE);
        }
    }

    private void HandleUpdateCredentialRequest(Message message)
    {

    }

    private void HandleLoginRequest(Message message)
    {

    }

    private void HandleLogoutRequest(Message message)
    {

    }

    private void HandleInsertLimitOrderRequest(Message message)
    {

    }

    private void HandleInsertMarketOrderRequest(Message message)
    {

    }

    private void HandleInsertStopOrderRequest(Message message)
    {

    }

    private void HandleCancelOrderRequest(Message message)
    {

    }

    private void HandleGetPriceHistoryRequest(Message message)
    {

    }
}
