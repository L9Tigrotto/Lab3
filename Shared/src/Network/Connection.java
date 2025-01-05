
package Network;

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

    public boolean IsAlive() { return _socket.isConnected(); }
    public boolean IsDataAvailable() throws IOException { return _dataInputStream.available() > 0; }

    public Message Receive() throws IOException
    {
        MessageType kind = MessageType.FromInt(_dataInputStream.readInt());
        String data = _dataInputStream.readUTF();

        return new Message(kind, data);
    }

    public void Send(ITransmittable transmittable) throws IOException
    {
        Message message = transmittable.ToMessage();
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
