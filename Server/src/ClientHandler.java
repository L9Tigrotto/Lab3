
import Messages.Message;
import Messages.MessageKind;
import Messages.TextMessage;
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
            TextMessage textMessage;
            Message message;

            for (int i = 0; i < 10; i++)
            {
                // request
                message = _connection.Receive();

                if (message.GetKind() != MessageKind.TextRequest)
                {
                    throw new SocketException();
                }

                //textMessage = message.ToTextMessage();
                //System.out.printf("[INFO] Received '%s'.\n",
                //  textMessage.GetText());

                // response
                textMessage = new TextMessage(
                        MessageKind.TextResponse,
                        "Ciao from server " + (i + 1));
                _connection.Send(textMessage.ToMessage());
            }
        }
        catch (IOException e) {
            return;
        }
    }
}
