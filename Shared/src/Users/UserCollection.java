
package Users;

import Helpers.Utilities;
import Messages.LoginRequest;
import Messages.LogoutRequest;
import Messages.RegisterRequest;
import Messages.SimpleResponse;
import com.google.gson.FormattingStyle;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserCollection
{
    private static final UserCollection _instance = new UserCollection();

    private final ConcurrentHashMap<String, User> _registered;
    private final ConcurrentHashMap<String, User> _connected;

    private UserCollection()
    {
        _registered = new ConcurrentHashMap<>();
        _connected = new ConcurrentHashMap<>();
    }

    private boolean IsRegisteredInternal(String username) { return _registered.containsKey(username); }
    private boolean IsConnectedInternal(String username) { return _connected.containsKey(username); }

    private User FromNameInternal(String username) throws UserNotRegisteredException
    {
        User user = _registered.get(username);
        if (user == null) { throw new UserNotRegisteredException(); }
        return user;
    }

    private SimpleResponse TryRegisterInternal(String username, String password)
    {
        // attempt to insert the new user into the collection. Handle the case where the username is already taken,
        // even though the initial check might have missed it. This could happen due to race conditions
        User user = new User(username, password);
        if (_registered.putIfAbsent(username, user) != null) { return RegisterRequest.USERNAME_NOT_AVAILABLE; }

        // if all checks pass and the user is successfully inserted, send an OK response
        return RegisterRequest.OK;
    }

    public SimpleResponse TryLoginInternal(User user, String password)
    {
        if (!user.MatchPassword(password)) { return LoginRequest.USERNAME_PASSWORD_MISMATCH; }
        else if (_connected.putIfAbsent(user.GetUsername(), user) == null) { return LoginRequest.OK; }
        else { return LoginRequest.USERNAME_PASSWORD_MISMATCH; }
    }

    public SimpleResponse TryLogoutInternal(User user)
    {
        if (_connected.remove(user.GetUsername()) == null) { return LogoutRequest.USER_NOT_LOGGED; }
        else { return LogoutRequest.OK; }
    }

    private void LoadInternal(String filename) throws IOException
    {
        File userFile = new File(filename);
        if (!userFile.exists()) { return; }

        try (FileReader fileReader = new FileReader(userFile);
             BufferedReader bufferedReader = new BufferedReader(fileReader);
             JsonReader jsonReader = new JsonReader(bufferedReader))
        {
            jsonReader.beginArray();

            while (jsonReader.hasNext())
            {
                jsonReader.beginObject();

                String name = Utilities.ReadString(jsonReader, "name");
                String password = Utilities.ReadString(jsonReader, "password");

                // avoid overriding duplicate users (shouldn't be any)
                _registered.putIfAbsent(name, new User(name, password));

                jsonReader.endObject();
            }

            jsonReader.endArray();
        }
    }

    private void SaveInternal(String filename) throws IOException
    {
        File userFile = new File(filename);
        if (!userFile.exists()) { return; }

        try (FileWriter fileWriter = new FileWriter(userFile);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             JsonWriter jsonWriter = new JsonWriter(bufferedWriter))
        {
            jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
            jsonWriter.beginArray();

            Set<Map.Entry<String, User>> entrySet = _registered.entrySet();

            // iterate through user map and write user data to JSON file
            for (Map.Entry<String, User> entry : entrySet) {
                jsonWriter.beginObject();
                jsonWriter.name("name").value(entry.getValue().GetUsername());
                jsonWriter.name("password").value(entry.getValue().GetPassword());
                jsonWriter.endObject();
            }

            jsonWriter.endArray();
        }
    }


    public static boolean IsRegistered(String username) { return _instance.IsRegisteredInternal(username); }
    public static boolean IsConnected(String username) { return _instance.IsConnectedInternal(username); }

    public static User FromName(String username) throws UserNotRegisteredException { return _instance.FromNameInternal(username); }
    public static SimpleResponse TryRegister(String username, String password) { return _instance.TryRegisterInternal(username, password); }
    public static SimpleResponse TryLogin(User user, String password) { return _instance.TryLoginInternal(user, password); }
    public static SimpleResponse TryLogout(User user) { return _instance.TryLogoutInternal(user); }


    public static void Load(String filename) throws IOException { _instance.LoadInternal(filename); }
    public static void Save(String filename) throws IOException { _instance.SaveInternal(filename); }
}
