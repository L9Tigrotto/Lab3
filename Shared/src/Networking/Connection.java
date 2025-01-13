
package Networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;

/**
 * This class represents a network connection over a TCP socket.
 * It provides methods for sending and receiving data in JSON format.
 */
public class Connection
{
    // the underlying Socket object representing the network connection
    private final Socket _socket;

    // a DataInputStream for reading data from the socket's input stream
    private final DataInputStream _dataInputStream;

    // a DataOutputStream for writing data to the socket's output stream
    private final DataOutputStream _dataOutputStream;

    /**
     * Creates a new Connection object for the specified socket.
     *
     * @param socket The Socket object representing the network connection.
     * @throws IOException If an error occurs while creating the streams.
     */
    public Connection(Socket socket) throws IOException
    {
        _socket = socket;
        _dataInputStream = new DataInputStream(socket.getInputStream());
        _dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * Checks if the underlying socket connection is closed.
     *
     * @return True if the socket is closed, false otherwise.
     */
    public boolean IsClosed() { return _socket.isClosed(); }

    /**
     * Checks if there is data available to be read from the socket.
     *
     * @return True if there is data available, false otherwise.
     * @throws IOException If an error occurs while checking for available data.
     */
    public boolean IsDataAvailable() throws IOException { return _dataInputStream.available() > 0; }

    /**
     * Receives a request object from the network connection.
     *
     * @return The Request object received from the network.
     * @throws IOException If an error occurs while reading data or parsing the JSON string.
     */
    public Request ReceiveRequest() throws IOException, ParseException { return Request.FromJson(_dataInputStream.readUTF()); }

    /**
     * Receives a response object from the network connection.
     *
     * @return The Response object received from the network.
     * @throws IOException If an error occurs while reading data or parsing the JSON string.
     */
    public Response ReceiveResponse() throws IOException { return Response.FromJson(_dataInputStream.readUTF()); }

    /**
     * Sends a request object over the network connection.
     *
     * @param request The Request object to be sent.
     * @throws IOException If an error occurs while writing data to the stream.
     */
    public void Send(Request request) throws IOException { _dataOutputStream.writeUTF(request.ToJson()); }

    /**
     * Sends a response object over the network connection.
     *
     * @param response The Response object to be sent.
     * @throws IOException If an error occurs while writing data to the stream.
     */
    public void Send(Response response) throws IOException { _dataOutputStream.writeUTF(response.ToJson()); }

    /**
     * This method ensures proper resource management by closing the input stream,
     * output stream, and the underlying socket.
     *
     * @throws IOException If an error occurs while closing the streams or the socket.
     */
    public void Close() throws IOException
    {
        _dataInputStream.close();
        _dataOutputStream.close();
        _socket.close();
    }
}
