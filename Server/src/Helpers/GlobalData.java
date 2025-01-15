
package Helpers;

import Messages.*;
import Networking.Listener;
import Orders.*;
import Users.User;
import Users.UserCollection;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * This class provides global access to shared data and functionality within the server application.
 * It manages server settings, registered users, and the server listener.
 */
public class GlobalData
{
    // the filename of the server configuration file
    private static final String CONFIG_FILENAME = "server.properties";

    // the server settings object, loaded from the configuration file
    public static final ServerSettings SETTINGS;

    // the listener object for handling incoming connections
    public static final Listener TCP_LISTENER;

    public static final DatagramSocket UPD_SOCKET;

    // static initializer block to data settings and users at startup
    static
    {
        try { SETTINGS = new ServerSettings(CONFIG_FILENAME); }
        catch (IOException e)
        {
            System.out.printf("[ERROR] Unable to load settings from file: %s\n", e.getMessage());
            throw new RuntimeException(e);
        }

        TCP_LISTENER = new Listener();

        try { UPD_SOCKET = new DatagramSocket(SETTINGS.SERVER_UDP_PORT);}
        catch (SocketException e)
        {
            System.out.printf("[ERROR] Unable to open UDP socket: %s\n", e.getMessage());
            throw new RuntimeException(e);
        }

        long lastUsedID;
        try { lastUsedID = HistoryRecordCollection.Load(SETTINGS.OrderHistoryFilename); }
        catch (IOException e)
        {
            System.out.printf("[ERROR] Unable to load orders history from file: %s\n", e.getMessage());
            throw new RuntimeException(e);
        }

        if (SETTINGS.NextOrderID <= lastUsedID) { SETTINGS.NextOrderID = lastUsedID + 1; }

        try { UserCollection.Load(SETTINGS.UsersFilename); }
        catch (IOException e) { System.out.printf("[ERROR] Unable to load users to from file: %s\n", e.getMessage()); }
    }

    public static MarketOrder CreateMarketOrder(MarketOrderRequest request, User user)
    {
        long orderID;
        synchronized (SETTINGS) { orderID = SETTINGS.NextOrderID++; }

        return MarketOrder.FromRequest(orderID, request, user);
    }

    public static LimitOrder CreateLimitOrder(LimitOrderRequest request, User user)
    {
        long orderID;
        synchronized (SETTINGS) { orderID = SETTINGS.NextOrderID++; }

        return LimitOrder.FromRequest(orderID, request, user);
    }

    public static StopOrder CreateStopOrder(StopOrderRequest request, User user)
    {
        long orderID;
        synchronized (SETTINGS) { orderID = SETTINGS.NextOrderID++; }

        return StopOrder.FromRequest(orderID, request, user);
    }

    /**
     * Saves server settings and user data to their respective files.
     */
    public static void Save()
    {
        try { SETTINGS.Save();}
        catch (IOException e) { System.out.printf("[ERROR] Unable to save settings to file: %s\n", e.getMessage()); }

        try { HistoryRecordCollection.Save(SETTINGS.OrderHistoryFilename); }
        catch (IOException e) { System.out.printf("[ERROR] Unable to save orders history to file: %s\n", e.getMessage()); }

        try { UserCollection.Save(SETTINGS.UsersFilename); }
        catch (IOException e) { System.out.printf("[ERROR] Unable to save users to file: %s\n", e.getMessage()); }
    }
}
