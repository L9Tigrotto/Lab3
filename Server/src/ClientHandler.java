
import DataStructures.User;
import Messages.Message;
import Messages.MessageKind;
import Messages.RegisterRequest;
import Messages.SimpleResponse;
import Network.Connection;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable
{
    private final Connection _connection;

    public ClientHandler(Socket socket) throws IOException
    {
        _connection = new Connection(socket);
    }

    @Override
    public void run()
    {
        try
        {
            for (int i = 0; i < 1; i++)
            {
                // request
                Message message = _connection.Receive();

                if (message.GetKind() != MessageKind.RegisterRequest)
                {
                    throw new SocketException();
                }

                RegisterRequest registerRequest = RegisterRequest.FromMessage(message);
                if (registerRequest.GetPassword().isEmpty())
                {
                    // send error
                }

                User.Insert(registerRequest.GetUsername(), registerRequest.GetPassword());

                SimpleResponse simpleResponse = new SimpleResponse(100, "OK");
                _connection.Send(simpleResponse.ToMessage(MessageKind.RegisterResponse));
            }
        }
        catch (IOException e) {
            return;
        }
    }
}
