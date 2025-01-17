
package Networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.text.ParseException;

/**
 * This class represents a network connection over both TCP and UDP sockets.
 * It provides methods for sending and receiving data in JSON format over TCP,
 * and sending notifications over UDP.
 */
public class Connection
{
    // the underlying Socket object representing the network connection
    private final Socket _socketTCP;

    // a DataInputStream for reading data from the socket's input stream
    private final DataInputStream _dataInputStream;

    // a DataOutputStream for writing data to the socket's output stream
    private final DataOutputStream _dataOutputStream;

    // the group address for sending UDP notifications
    private final InetAddress _groupAddress;

    // the UDP port for sending notifications
    private final int _groupPort;

    // datagramSocket for sending UDP notifications (only used on server)
    private final DatagramSocket _socketUDP;

    /**
     * Creates a new Connection object for the specified TCP socket and UDP socket.
     * Initializes the necessary input/output streams for TCP communication,
     * and sets up the UDP group address and port for sending notifications.
     *
     * @param socketTCP The Socket object representing the TCP connection.
     * @param socketUDP The DatagramSocket object used for UDP communication.
     * @param groupAddress The group address for sending UDP notifications.
     * @param groupIP The UDP port for sending notifications.
     * @throws IOException If an error occurs while creating streams or initializing sockets.
     */
    public Connection(Socket socketTCP, DatagramSocket socketUDP, String groupAddress, int groupIP) throws IOException
    {
        // initialize the TCP socket and associated streams
        _socketTCP = socketTCP;
        _dataInputStream = new DataInputStream(socketTCP.getInputStream());
        _dataOutputStream = new DataOutputStream(socketTCP.getOutputStream());

        // set up the group address and port for UDP notifications
        _groupAddress = InetAddress.getByName(groupAddress);
        _groupPort = groupIP;
        _socketUDP = socketUDP;
    }

    /**
     * Checks if the underlying TCP socket connection is closed.
     *
     * @return True if the TCP socket is closed, false otherwise.
     */
    public boolean IsClosed() { return _socketTCP.isClosed(); }

    /**
     * Checks if there is data available to be read from the TCP socket's input stream.
     *
     * @return True if there is data available to read, false otherwise.
     * @throws IOException If an error occurs while checking the stream.
     */
    public boolean IsDataAvailable() throws IOException { return _dataInputStream.available() > 0; }

    /**
     * Receives a Request object from the network connection.
     * This method reads the incoming data, parses it as a UTF string,
     * and converts it from JSON to a Request object.
     *
     * @return The Request object received from the network.
     * @throws IOException If an error occurs while reading data.
     * @throws ParseException If the JSON data cannot be parsed correctly.
     */
    public Request ReceiveRequest() throws IOException, ParseException { return Request.FromJson(_dataInputStream.readUTF()); }

    /**
     * Receives a Response object from the network connection.
     * This method reads the incoming data, parses it as a UTF string,
     * and converts it from JSON to a Response object.
     *
     * @return The Response object received from the network.
     * @throws IOException If an error occurs while reading data.
     */
    public Response ReceiveResponse() throws IOException { return Response.FromJson(_dataInputStream.readUTF()); }

    /**
     * Sends a Request object over the TCP connection.
     * This method serializes the Request object to JSON and writes it to the output stream.
     *
     * @param request The Request object to be sent.
     * @throws IOException If an error occurs while writing data to the stream.
     */
    public void Send(Request request) throws IOException
    {
        _dataOutputStream.writeUTF(request.ToJson());
        _dataOutputStream.flush();
    }

    /**
     * Sends a Response object over the TCP connection.
     * This method serializes the Response object to JSON and writes it to the output stream.
     *
     * @param response The Response object to be sent.
     * @throws IOException If an error occurs while writing data to the stream.
     */
    public void Send(Response response) throws IOException
    {
        _dataOutputStream.writeUTF(response.ToJson());
        _dataOutputStream.flush();
    }

    /**
     * Sends a notification to the client over UDP.
     * This method creates a DatagramPacket with the notification message and sends it to the specified UDP group address and port.
     *
     * @param notification The notification message to be sent.
     */
    public void SendNotification(String notification)
    {
        // convert the notification string to bytes and create a DatagramPacket to send the notification to the client
        byte[] buffer = notification.getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, _groupAddress, _groupPort);

        try { _socketUDP.send(datagramPacket); }
        catch (IOException e)
        {
            System.out.printf("[ERROR] Unable to send notification to %s:%d: %s", _groupAddress, _groupPort, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes the network connection by properly closing the input/output streams
     * and the underlying TCP and UDP sockets.
     *
     * @throws IOException If an error occurs while closing the streams or sockets.
     */
    public void Close() throws IOException
    {
        _dataInputStream.close();
        _dataOutputStream.close();
        _socketTCP.close();
        if (_socketUDP != null) { _socketUDP.close(); }
    }
}
