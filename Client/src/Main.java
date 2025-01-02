
import Messages.Message;
import Messages.MessageKind;
import Messages.TextMessage;
import Network.Connection;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        Socket socket;
        try { socket = Utilities.TryConnect(
                GlobalData.SETTINGS.TCP_IP,
                GlobalData.SETTINGS.TCP_PORT,
                GlobalData.SETTINGS.ConnectionRetries,
                GlobalData.SETTINGS.ConnectionRetryTimeoutMS); }
        catch (Exception e) { return; }

        Connection connection = new Connection(socket);
        TextMessage textMessage;
        Message message;

        for (int i = 0; i < 10; i++)
        {
            // request
            textMessage = new TextMessage(
                    MessageKind.TextRequest,
                    "Ciao from client " + (i + 1));
            connection.Send(textMessage.ToMessage());

            // response
            message = connection.Receive();

            if (message.GetKind() != MessageKind.TextResponse)
            {
                throw new SocketException();
            }

            //textMessage = message.ToTextMessage();
            System.out.printf("[INFO] Received '%s'.\n",
                    textMessage.GetText());
        }
    }
}
