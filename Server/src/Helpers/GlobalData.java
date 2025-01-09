package Helpers;

import Messages.RegisterRequest;
import Messages.SimpleResponse;
import Networking.Listener;
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class holds global data and settings for the server application.
 */
public class GlobalData
{
    // the filename of the server configuration file
    private static final String CONFIG_FILENAME = "server.properties";
    private static final String USERS_FILENAME= "users.json";

    // the server settings object, loaded from the configuration file
    public static final ServerSettings SETTINGS;

    // the listener object for handling incoming connections
    public static final Listener LISTENER;

    public static final ConcurrentHashMap<String, User> USERS = new ConcurrentHashMap<String, User>();


    static
    {
        try { SETTINGS = new ServerSettings(CONFIG_FILENAME); }
        catch (IOException e) { throw new RuntimeException(e); }

        File file = new File(USERS_FILENAME);

        if (file.exists())
        {
            try(FileReader fileReader = new FileReader(file);
                JsonReader jsonReader = new JsonReader(fileReader))
            {
                jsonReader.beginArray();

                while (jsonReader.hasNext())
                {
                    jsonReader.beginObject();

                    if (!jsonReader.nextName().equals("name")) { throw new JsonIOException(""); }
                    String name = jsonReader.nextString();
                    if (!jsonReader.nextName().equals("password")) { throw new JsonIOException(""); }
                    String password = jsonReader.nextString();

                    if (USERS.containsKey(name)) { continue; }
                    USERS.put(name, new User(name, password));

                    jsonReader.endObject();
                }

                jsonReader.endArray();
            }
            catch (IOException e) { throw new RuntimeException(e); }
        }

        LISTENER = new Listener();
    }

    public static boolean UserExists(String name) { return USERS.containsKey(name); }

    public static User UserFromName(String name) throws UserNotRegisteredException
    {
        User user = USERS.get(name);
        if (user == null) { throw new UserNotRegisteredException(); }
        return user;
    }

    public static SimpleResponse TryRegisterUser(String username, String password)
    {
        // attempt to insert the new user into the collection. Handle the case where the username is already taken,
        // even though the initial check might have missed it. This could happen due to race conditions
        User user = new User(username, password);
        if (USERS.putIfAbsent(username, user) != null) { return RegisterRequest.USERNAME_NOT_AVAILABLE; }

        // if all checks pass and the user is successfully inserted, send an OK response
        return RegisterRequest.OK;
    }

    public static void Save()
    {
        File file = new File(USERS_FILENAME);

        try(FileWriter fileWriter = new FileWriter(file);
            JsonWriter jsonWriter = new JsonWriter(fileWriter))
        {
            jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
            jsonWriter.beginArray();

            Set<Map.Entry<String, User>> entrySet = USERS.entrySet();
            for (Map.Entry<String, User> entry : entrySet) {
                jsonWriter.beginObject();
                jsonWriter.name("name").value(entry.getValue().GetUsername());
                jsonWriter.name("password").value(entry.getValue().GetPassword());
                jsonWriter.endObject();
            }

            jsonWriter.endArray();
        }
        catch (IOException e) { throw new RuntimeException(e); }
    }
}
