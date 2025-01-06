
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

    public Request ReceiveRequest() throws IOException { return Request.FromJson(_dataInputStream.readUTF()); }
    public Response ReceiveResponse() throws IOException { return Response.FromJson(_dataInputStream.readUTF()); }
    public void Send(Request request) throws IOException { _dataOutputStream.writeUTF(request.ToJson()); }
    public void Send(Response response) throws IOException { _dataOutputStream.writeUTF(response.ToJson()); }

    public void Close() throws IOException
    {
        _dataInputStream.close();
        _dataOutputStream.close();
        _socket.close();
    }
}
