
import Messages.Message;
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
        while(_connection.IsAlive())
        {
            try
            {
                Message message = _connection.Receive();

                // update the last message time to prevent the daemon thread from terminating this task due to
                // inactivity during the operation
                _lastMessageTime.set(System.currentTimeMillis());

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
                return;
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
                return;
            }
        }
    }

    private void HandleRegisterRequest(Message message)
    {

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
