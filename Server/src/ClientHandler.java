
import DataStructures.Connection;

import java.io.IOException;

public class ClientHandler implements Runnable
{
    private final Connection _connection;

    public ClientHandler(Connection connection) {
        _connection = connection;
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
