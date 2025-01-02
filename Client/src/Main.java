
import Messages.Message;
import Messages.MessageKind;
import Messages.TextMessage;
import Network.Connection;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;

public class Main
{
    private static final String CONFIG_FILENAME = "client.properties";
    public static final ClientSettings SETTINGS;

    static
    {
        try { SETTINGS = new ClientSettings(CONFIG_FILENAME); }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    public static void main(String[] args) throws IOException
    {
        Socket socket;
        try { socket = Utilities.TryConnect(
                SETTINGS.TCP_IP,
                SETTINGS.TCP_PORT,
                SETTINGS.ConnectionRetries,
                SETTINGS.ConnectionRetryTimeoutMS); }
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

            textMessage = message.ToTextMessage();
            System.out.printf("[INFO] Received '%s'.\n",
                    textMessage.GetText());
        }
    }
}
