
package Helpers;

import Messages.*;
import Networking.Listener;
import Orders.*;
import Users.User;
import Users.UserCollection;
import Users.UserNotRegisteredException;
import com.google.gson.FormattingStyle;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
    public static final Listener LISTENER;

    // static initializer block to data settings and users at startup
    static
    {
        try { SETTINGS = new ServerSettings(CONFIG_FILENAME); }
        catch (IOException e)
        {
            System.out.printf("[ERROR] Unable to load settings from file: %s\n", e.getMessage());
            throw new RuntimeException(e);
        }

        LISTENER = new Listener();

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
