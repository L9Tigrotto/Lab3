
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

    public boolean IsDataAvailable() throws IOException { return _dataInputStream.available() > 0; }

    public ITransmittable ReceiveRequest() throws IOException
    {
        return Request.FromJson(_dataInputStream.readUTF());
    }

    public ITransmittable ReceiveResponse() throws IOException
    {
        return Response.FromJson(_dataInputStream.readUTF());
    }

    public void SendRequest(ITransmittable transmittable) throws IOException
    {
        _dataOutputStream.writeUTF(Request.ToJson(transmittable));
    }

    public void SendResponse(ITransmittable transmittable) throws IOException
    {
        _dataOutputStream.writeUTF(Response.ToJson(transmittable));
    }

    public void Close() throws IOException
    {
        _dataInputStream.close();
        _dataOutputStream.close();
        _socket.close();
    }
}
