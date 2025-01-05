
package Network;

import Messages.Message;
import Messages.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection
{
    protected final Socket _socket;
    protected final DataInputStream _dataInputStream;
    protected final DataOutputStream _dataOutputStream;

    public Connection(Socket socket) throws IOException
    {
        _socket = socket;
        _dataInputStream = new DataInputStream(socket.getInputStream());
        _dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    public Message Receive() throws IOException
    {
        MessageType kind = MessageType.FromInt(_dataInputStream.readInt());
        String data = _dataInputStream.readUTF();

        return new Message(kind, data);
    }

    public void Send(Message message) throws IOException
    {
        _dataOutputStream.writeInt(message.GetType().ToInt());
        _dataOutputStream.writeUTF(message.GetData());
    }

    public void Close() throws IOException
    {
        _dataInputStream.close();
        _dataOutputStream.close();
        _socket.close();
    }
}
