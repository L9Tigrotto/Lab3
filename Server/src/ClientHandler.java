
import DataStructures.Connection;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable
{
    private final Connection _connection;

    public ClientHandler(Socket clientSocket) throws IOException
    {
        _connection = new Connection(clientSocket);
    }

    @Override
    public void run()
    {
        try
        {
            int number = _connection.ReceiveInt();

            while (true)
            {
                _connection.Send(number);
                number = _connection.ReceiveInt() + 1;
                System.out.println(number);
            }
        }
        catch (IOException e) {
            return;
        }
    }
}
