
package Helpers;

import Messages.*;
import Networking.Listener;
import Orders.*;
import Users.User;
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
    private static final String USERS_FILENAME = "users.json";
    private static final String ORDER_HISTORY_FILENAME=  "orderHistory.json";

    // the server settings object, loaded from the configuration file
    public static final ServerSettings SETTINGS;

    // the listener object for handling incoming connections
    public static final Listener LISTENER;

    // a concurrent hash map to store registered users (username as key, User object as value)
    public static final ConcurrentHashMap<String, User> USERS;

    // first level list ordered by year and month
    // second level list ordered by day
    // [
    //      2021/01 : [
    //          { price history 1 },
    //          { price history 2 },
    //          ...
    //      ],
    //      ...
    //      2021/03 : [
    //          { price history 1 },
    //          { price history 2 },
    //      ...
    //      ],
    //      ...
    // ]
    public static final TreeSet<Tuple<Long, TreeSet<Tuple<Long, HistoryRecord>>>> ORDER_HISTORY;

    // static initializer block to data settings and users at startup
    static
    {
        try { SETTINGS = new ServerSettings(CONFIG_FILENAME); }
        catch (IOException e)
        {
            System.out.printf("[ERROR] Unable to load settings file: %s\n", e.getMessage());
            throw new RuntimeException(e);
        }


        LISTENER = new Listener();

        File orderHistoryFile = new File(ORDER_HISTORY_FILENAME);
        ORDER_HISTORY = new TreeSet<>(
                (tuple1, tuple2) ->
                {
                    Calendar cal = Calendar.getInstance();

                    cal.setTimeInMillis(tuple1.GetX());
                    int month1 = cal.get(Calendar.MONTH);
                    int year1 = cal.get(Calendar.YEAR);

                    cal.setTimeInMillis(tuple2.GetX());
                    int month2 = cal.get(Calendar.MONTH);
                    int year2 = cal.get(Calendar.YEAR);

                    int comp = Integer.compare(year1, year2);
                    if (comp == 0) { comp = Integer.compare(month1, month2); }
                    return comp;
                }
        );

        if (orderHistoryFile.exists())
        {
            try (FileReader fileReader = new FileReader(orderHistoryFile);
                JsonReader jsonReader = new JsonReader(fileReader))
            {
                jsonReader.beginObject();

                if (!jsonReader.nextName().equals("trades")) { throw new JsonIOException(""); }
                jsonReader.beginArray();

                while (jsonReader.hasNext())
                {
                    jsonReader.beginObject();

                    long orderID = Utilities.ReadLong(jsonReader, "orderID");
                    Method method = Method.FromString(Utilities.ReadString(jsonReader, "type"));
                    Type type = Type.FromString(Utilities.ReadString(jsonReader, "orderType"));
                    long size = Utilities.ReadLong(jsonReader, "size");
                    long price = Utilities.ReadLong(jsonReader, "price");
                    long timestamp = Utilities.ReadLong(jsonReader, "timestamp");

                    // check if te current month/year exists in the list
                    Tuple<Long, TreeSet<Tuple<Long, HistoryRecord>>> dummy = new Tuple<>(timestamp, null);
                    Tuple<Long, TreeSet<Tuple<Long, HistoryRecord>>> firstLevelTuple;
                    TreeSet<Tuple<Long, HistoryRecord>> secondLevelTuple;

                    if (ORDER_HISTORY.contains(dummy))
                    {
                        firstLevelTuple = ORDER_HISTORY.ceiling(dummy);
                        secondLevelTuple = firstLevelTuple.GetY();
                    }
                    else
                    {
                        secondLevelTuple = CreateSecondLevelOrderHistory();
                        firstLevelTuple = new Tuple<>(timestamp, secondLevelTuple);
                        ORDER_HISTORY.add(firstLevelTuple);
                    }

                    HistoryRecord historyRecord = new HistoryRecord(orderID, method, type, size, price, timestamp);
                    Tuple<Long, HistoryRecord> tuple = new Tuple<>(timestamp, historyRecord);
                    secondLevelTuple.add(tuple);

                    jsonReader.endObject();
                }

                jsonReader.endArray();
                jsonReader.endObject();
            }
            catch (IOException e) { System.out.printf("[ERROR] Unable to load orderHistory file: %s\n", e.getMessage()); }
        }


        File usersFile = new File(USERS_FILENAME);
        USERS = new ConcurrentHashMap<String, User>();

        if (usersFile.exists())
        {
            try (FileReader fileReader = new FileReader(usersFile);
                JsonReader jsonReader = new JsonReader(fileReader))
            {
                jsonReader.beginArray();

                while (jsonReader.hasNext())
                {
                    jsonReader.beginObject();

                    String name = Utilities.ReadString(jsonReader, "name");
                    String password = Utilities.ReadString(jsonReader, "password");

                    // avoid overriding duplicate users (shouldn't be any)
                    if (USERS.containsKey(name)) { continue; }
                    USERS.put(name, new User(name, password));

                    jsonReader.endObject();
                }

                jsonReader.endArray();
            }
            catch (IOException e) { System.out.printf("[ERROR] Unable to load users file: %s\n", e.getMessage()); }
        }
    }

    private static TreeSet<Tuple<Long, HistoryRecord>> CreateSecondLevelOrderHistory()
    {
        return new TreeSet<>(
                (tuple1, tuple2) ->
                {
                    Calendar cal = Calendar.getInstance();

                    cal.setTimeInMillis(tuple1.GetX());
                    int day1 = cal.get(Calendar.DAY_OF_MONTH);

                    cal.setTimeInMillis(tuple2.GetX());
                    int day2 = cal.get(Calendar.DAY_OF_MONTH);

                    return Integer.compare(day1, day2);
                }
        );
    }

    /**
     * Checks if a user with the given usernamename exists in the registered users map.
     *
     * @param username The username to check.
     * @return True if the user exists, false otherwise.
     */
    public static boolean UserExists(String username) { return USERS.containsKey(username); }

    /**
     * Retrieves a User object from the registered users map based on the given username.
     *
     * @param username The username of the user to retrieve.
     * @return The User object if found, otherwise throws a UserNotRegisteredException.
     * @throws UserNotRegisteredException If no user with the given username is found.
     */
    public static User UserFromName(String username) throws UserNotRegisteredException
    {
        User user = USERS.get(username);
        if (user == null) { throw new UserNotRegisteredException(); }
        return user;
    }

    /**
     * Attempts to register a new user.
     *
     * @param username The desired username for the new user.
     * @param password The password for the new user.
     * @return SimpleResponse.OK if registration is successful,
     *         SimpleResponse.USERNAME_NOT_AVAILABLE if the username is already taken.
     */
    public static SimpleResponse TryRegisterUser(String username, String password)
    {
        // attempt to insert the new user into the collection. Handle the case where the username is already taken,
        // even though the initial check might have missed it. This could happen due to race conditions
        User user = new User(username, password);
        if (USERS.putIfAbsent(username, user) != null) { return RegisterRequest.USERNAME_NOT_AVAILABLE; }

        // if all checks pass and the user is successfully inserted, send an OK response
        return RegisterRequest.OK;
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

        File file = new File(USERS_FILENAME);

        try(FileWriter fileWriter = new FileWriter(file);
            JsonWriter jsonWriter = new JsonWriter(fileWriter))
        {
            jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
            jsonWriter.beginArray();

            Set<Map.Entry<String, User>> entrySet = USERS.entrySet();

            // iterate through user map and write user data to JSON file
            for (Map.Entry<String, User> entry : entrySet) {
                jsonWriter.beginObject();
                jsonWriter.name("name").value(entry.getValue().GetUsername());
                jsonWriter.name("password").value(entry.getValue().GetPassword());
                jsonWriter.endObject();
            }

            jsonWriter.endArray();
        }
        catch (IOException e) { System.out.printf("[ERROR] Unable to save users to file: %s\n", e.getMessage()); }
    }
}
