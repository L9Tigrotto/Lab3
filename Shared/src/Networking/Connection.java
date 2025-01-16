
package Networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.text.ParseException;

/**
 * This class represents a network connection over a TCP socket.
 * It provides methods for sending and receiving data in JSON format.
 * It also supports sending notifications via UDP.
 */
public class Connection
{
    // the underlying Socket object representing the network connection
    private final Socket _socketTCP;

    // a DataInputStream for reading data from the socket's input stream
    private final DataInputStream _dataInputStream;

    // a DataOutputStream for writing data to the socket's output stream
    private final DataOutputStream _dataOutputStream;

    // the underlying DatagramSocket object for sending UDP packets
    private final DatagramSocket _socket_UDP;

    // the UDP port for the client to send notifications
    private final int _clientUDPPort;

    /**
     * Creates a new Connection object for the specified socket.
     * This constructor initializes both TCP and UDP connections.
     *
     * @param socketTCP The Socket object representing the network connection.
     * @param socket_UDP The DatagramSocket for sending UDP notifications.
     * @param clientUDPPort The UDP port number for client notifications.
     * @throws IOException If an error occurs while creating the streams or initializing the sockets.
     */
    public Connection(Socket socketTCP, DatagramSocket socket_UDP, int clientUDPPort) throws IOException
    {
        _socketTCP = socketTCP;
        _dataInputStream = new DataInputStream(socketTCP.getInputStream());
        _dataOutputStream = new DataOutputStream(socketTCP.getOutputStream());

        _socket_UDP = socket_UDP;
        _clientUDPPort = clientUDPPort;
    }

    /**
     * Checks if the underlying socket connection is closed.
     *
     * @return True if the socket is closed, false otherwise.
     */
    public boolean IsClosed() { return _socketTCP.isClosed(); }

    /**
     * Checks if there is data available to be read from the socket's input stream.
     *
     * @return True if there is data available, false otherwise.
     * @throws IOException If an error occurs while checking for available data.
     */
    public boolean IsDataAvailable() throws IOException { return _dataInputStream.available() > 0; }

    /**
     * Receives a request object from the network connection.
     * Reads the incoming data and converts it from a JSON string to a Request object.
     *
     * @return The Request object received from the network.
     * @throws IOException If an error occurs while reading data or parsing the JSON string.
     * @throws ParseException If the JSON data is not correctly formatted.
     */
    public Request ReceiveRequest() throws IOException, ParseException { return Request.FromJson(_dataInputStream.readUTF()); }

    /**
     * Receives a response object from the network connection.
     * Reads the incoming data and converts it from a JSON string to a Response object.
     *
     * @return The Response object received from the network.
     * @throws IOException If an error occurs while reading data or parsing the JSON string.
     */
    public Response ReceiveResponse() throws IOException { return Response.FromJson(_dataInputStream.readUTF()); }

    /**
     * Sends a response object over the network connection.
     * Converts the Response object to a JSON string and writes it to the output stream.
     *
     * @param response The Response object to be sent.
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
     * Sends a notification to the client over UDP.
     * This method creates a DatagramPacket containing the notification message and sends it to the specified UDP port.
     *
     * @param notification The notification message to be sent.
     */
    public void SendNotification(String notification)
    {
        // convert the notification string to bytes and create a DatagramPacket to send the notification to the client
        byte[] buffer = notification.getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, _socketTCP.getInetAddress(), _clientUDPPort);

        // send the DatagramPacket over the UDP socket
        try { _socket_UDP.send(datagramPacket); }
        catch (IOException e) { System.out.println("[ERROR] Unable to send notification"); }
    }

    /**
     * Ensures proper resource management by closing the input stream, output stream,
     * and the underlying socket connection.
     *
     * @throws IOException If an error occurs while closing the streams or the socket.
     */
    public void Close() throws IOException
    {
        _dataInputStream.close();
        _dataOutputStream.close();
        _socketTCP.close();
    }
}
