
package Helpers;

import Messages.ClosedTradesNotification;
import Orders.Order;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.List;

/**
 * This class holds global data and settings for the client application.
 */
public class GlobalData
{
    // the filename of the client configuration file
    private static final String CONFIG_FILENAME = "client.properties";

    // the client settings object, loaded from the configuration file
    public static final ClientSettings SETTINGS;

    public static final MulticastSocket MULTICAST_SOCKET;

    public static final Thread NOTIFICATION_THREAD;

    public static final HashSet<Long> ORDER_IDS;

    // static initializer block to load client settings at startup
    static
    {
        try { SETTINGS = new ClientSettings(CONFIG_FILENAME); }
        catch (IOException e) { throw new RuntimeException(e); }

        try
        {
            MULTICAST_SOCKET = new MulticastSocket(SETTINGS.MULTICAST_PORT);
            MULTICAST_SOCKET.setSoTimeout(5000);

            // if create two instances of the program on the same host without socket set to
            // true, a BindException is thrown
            MULTICAST_SOCKET.setReuseAddress(true);

            InetAddress address = InetAddress.getByName(SETTINGS.MULTICAST_IP);
            MULTICAST_SOCKET.joinGroup(address);
        }
        catch (IOException e)
        {
            System.out.printf("[ERROR] Unable to connect to multicast group: %s\n", e.getMessage());
            throw new RuntimeException(e);
        }

        ORDER_IDS = new HashSet<>();

        NOTIFICATION_THREAD = new Thread(() ->
        {
            byte[] receiveBuffer = new byte[512 * 1024]; // 512 KB
            DatagramPacket datagramPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

            while (!Thread.currentThread().isInterrupted())
            {
                try { MULTICAST_SOCKET.receive(datagramPacket); }
                catch (SocketTimeoutException e) { continue; }
                catch (IOException e) { throw new RuntimeException(e); }

                String data = new String(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength());

                List<Order> orders = ClosedTradesNotification.DeserializeContent(data);
                StringBuilder builder = new StringBuilder();
                synchronized (ORDER_IDS)
                {
                    builder.append("trades: [\n");
                    for (Order order : orders)
                    {
                        if (ORDER_IDS.contains(order.GetID()))
                        {
                            builder.append("\t{\n");
                            builder.append(String.format("\t\tID: %d\n", order.GetID()));
                            builder.append(String.format("\t\ttype: %s\n", order.GetType().ToString()));
                            builder.append(String.format("\t\torderType: %s\n", order.GetMethod().ToString()));
                            builder.append(String.format("\t\tprice: %d\n", order.GetPrice()));
                            builder.append(String.format("\t\tsize: %d\n", order.GetSize()));
                            builder.append("\t}");
                        }
                    }
                    builder.append("]");
                }

                if (!orders.isEmpty()) { System.out.printf("[INFO] Received data:\n %s\n", builder); }
            }

            try
            {
                InetAddress address = InetAddress.getByName(SETTINGS.MULTICAST_IP);
                MULTICAST_SOCKET.leaveGroup(address);
            }
            catch (IOException e)
            {
                System.out.printf("[ERROR] Unable to leave multicast group: %s\n", e.getMessage());
                throw new RuntimeException(e);
            }

            MULTICAST_SOCKET.close();
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
