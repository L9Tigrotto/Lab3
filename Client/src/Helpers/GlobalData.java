
package Helpers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * This class holds global data and settings for the client application.
 */
public class GlobalData
{
    // the filename of the client configuration file
    private static final String CONFIG_FILENAME = "client.properties";

    // the client settings object, loaded from the configuration file
    public static final ClientSettings SETTINGS;

    public static final DatagramSocket UPD_SOCKET;

    public static final Thread NOTIFICATION_THREAD;

    // static initializer block to load client settings at startup
    static
    {
        try { SETTINGS = new ClientSettings(CONFIG_FILENAME); }
        catch (IOException e) { throw new RuntimeException(e); }

        try
        {
            UPD_SOCKET = new DatagramSocket(SETTINGS.CLIENT_UDP_PORT);
            UPD_SOCKET.setSoTimeout(5000);
        }
        catch (SocketException e)
        {
            System.out.printf("[ERROR] Unable to open UDP socket: %s\n", e.getMessage());
            throw new RuntimeException(e);
        }

        NOTIFICATION_THREAD = new Thread(() ->
        {
            byte[] receiveBuffer = new byte[5 * 1024 * 1024]; // 5 MB
            DatagramPacket datagramPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            while (!Thread.currentThread().isInterrupted())
            {
                try { UPD_SOCKET.receive(datagramPacket); }
                catch (SocketTimeoutException e) { continue; }
                catch (IOException e) { throw new RuntimeException(e); }

                String data = new String(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength());
                System.out.printf("[INFO] Received data:\n %s\n", data);
            }

            UPD_SOCKET.close();
        });
        NOTIFICATION_THREAD.start();
    }

    /**
     * Saves client settings to the configuration file.
     */
    public static void Save()
    {
        try { SETTINGS.Save();}
        catch (IOException e) { System.out.printf("[ERROR] Unable to save settings to file: %s\n", e.getMessage()); }
    }
}
