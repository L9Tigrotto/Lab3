
import Network.ITransmittable;
import Users.DuplicateUserException;
import Users.User;
import Network.Request;
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
                ITransmittable transmittable = _connection.ReceiveRequest();

                // filter the request type and begin working on the response
                switch (transmittable.GetOperation())
                {
                    case "register" -> HandleRegisterRequest((RegisterRequest) transmittable);
                    case "login" -> HandleUpdateCredentialRequest(transmittable);
                    case "logout" -> HandleLoginRequest(transmittable);
                    case "insertLimitOrder" -> HandleLogoutRequest(transmittable);
                    case "insertMarketOrder" -> HandleInsertLimitOrderRequest(transmittable);
                    case "insertStopOrder" -> HandleInsertMarketOrderRequest(transmittable);
                    case "cancelOrder" -> HandleInsertStopOrderRequest(transmittable);
                    case "getPriceHistory" -> HandleCancelOrderRequest(transmittable);
                    case "closedTrades" -> HandleGetPriceHistoryRequest(transmittable);
                    default ->
                    {
                        System.out.printf("[Info] Received unknown request message %s, ignoring\n", transmittable.GetOperation());
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
                _connection.SendResponse(RegisterRequest.INVALID_PASSWORD_RESPONSE);
                return;
            }

            if (!register.IsUsernameValid() && !User.Exists(username))
            {
                _connection.SendResponse(RegisterRequest.USERNAME_NOT_AVAILABLE_RESPONSE);
                return;
            }

            try { User.Insert(username, password); }
            catch (DuplicateUserException e)
            {
                _connection.SendResponse(RegisterRequest.USERNAME_NOT_AVAILABLE_RESPONSE);
                return;
            }

            _connection.SendResponse(RegisterRequest.OK_RESPONSE);
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
            _connection.SendResponse(RegisterRequest.OTHER_ERROR_CASES_RESPONSE);
        }
    }

    private void HandleUpdateCredentialRequest(ITransmittable transmittable) throws IOException
    {

    }

    private void HandleLoginRequest(ITransmittable transmittable)
    {

    }

    private void HandleLogoutRequest(ITransmittable transmittable)
    {

    }

    private void HandleInsertLimitOrderRequest(ITransmittable transmittable)
    {

    }

    private void HandleInsertMarketOrderRequest(ITransmittable transmittable)
    {

    }

    private void HandleInsertStopOrderRequest(ITransmittable transmittable)
    {

    }

    private void HandleCancelOrderRequest(ITransmittable transmittable)
    {

    }

    private void HandleGetPriceHistoryRequest(ITransmittable transmittable)
    {

    }
}
