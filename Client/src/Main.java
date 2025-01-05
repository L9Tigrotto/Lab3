
import Messages.Message;
import Messages.MessageKind;
import Messages.RegisterRequest;
import Messages.SimpleResponse;
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

        for (int i = 0; i < 1; i++)
        {
            // request
            RegisterRequest registerRequest = new RegisterRequest("Leo", "1234");
            connection.Send(registerRequest.ToMessage());

            // response
            Message message = connection.Receive();
            SimpleResponse simpleResponse = SimpleResponse.FromMessage(message);

            System.out.printf("[INFO] Received '%s'.\n",
                    simpleResponse.GetErrorMessage());
        }

        connection.Close();
    }
}
