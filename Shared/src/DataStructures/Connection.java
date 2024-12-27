package DataStructures;

import DataStructures.Messages.Kind;
import DataStructures.Messages.Message;

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
        Kind kind = Kind.FromInt(_dataInputStream.readInt());
        String data = _dataInputStream.readUTF();

        return new Message(kind, data);
    }

    public void Send(Message message) throws IOException
    {
        _dataOutputStream.writeInt(message.GetKind().GetCode());
        _dataOutputStream.writeUTF(message.GetData());
    }

    public void Send(int number) throws IOException
    {
        _dataOutputStream.writeInt(number);
    }

    public int ReceiveInt() throws IOException
    {
        return _dataInputStream.readInt();
    }

    public void Close() throws IOException
    {
        _socket.close();
    }


}
