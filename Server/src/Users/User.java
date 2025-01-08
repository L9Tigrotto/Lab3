
package Users;

import Messages.*;
import Network.Response;
import com.google.gson.FormattingStyle;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class User
{
    private static final String SAVE_FILE = "users.json";
    private static final ConcurrentHashMap<String, User> _users = new ConcurrentHashMap<String, User>();

    static
    {
        File file = new File(SAVE_FILE);

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

                    if (_users.containsKey(name)) { continue; }
                    _users.put(name, new User(name, password));

                    jsonReader.endObject();
                }

                jsonReader.endArray();
            }
            catch (IOException e) { throw new RuntimeException(e); }
        }
    }

    private final String _username;
    private String _password;
    private transient boolean _isConnected; // not serialized

    private User(String name, String password)
    {
        _username = name;
        _password = password;
        _isConnected = false;
    }

    public boolean MatchPassword(String newPassword) { return _password.equals(newPassword); }

    public boolean IsConnected() { return _isConnected; }

    public static SimpleResponse TryRegister(String username, String password)
    {
        // attempt to insert the new user into the collection. Handle the case where the username is already taken,
        // even though the initial check might have missed it. This could happen due to race conditions
        User user = new User(username, password);
        if (_users.putIfAbsent(username, user) != null) { return RegisterRequest.USERNAME_NOT_AVAILABLE; }

        // if all checks pass and the user is successfully inserted, send an OK response
        return RegisterRequest.OK;
    }

    public SimpleResponse TryUpdatePassword(String oldPassword, String newPassword)
    {
        // synchronize this block to prevent race conditions when multiple requests attempt to modify the
        // same user concurrently
        synchronized (this)
        {
            // verify that the provided old password matches the user's current password
            if (!MatchPassword(oldPassword)) { return UpdateCredentialsRequest.USERNAME_OLDPASSWORD_MISMATCH; }

            // prevent the user from setting the new password to the same as the old password
            if (MatchPassword(newPassword)) { return UpdateCredentialsRequest.NEW_AND_OLD_PASSWORD_EQUAL; }

            // specifically checks if the target user is connected to another session, not just whether any user is
            // connected to this particular ClientHandler instance
            if (_isConnected) { return UpdateCredentialsRequest.USER_LOGGED_IN; }

            // update the user's password with the new password
            _password = newPassword;
        }

        return UpdateCredentialsRequest.OK;
    }

    public SimpleResponse TryLogIn(String password)
    {
        synchronized (this)
        {
            // verify that the provided password matches the user's stored password
            if (!MatchPassword(password)) { return LoginRequest.USERNAME_PASSWORD_MISMATCH; }

            // check if the user is already logged in from another session
            if (_isConnected) { return LoginRequest.USER_ALREADY_LOGGED_IN; }

            // mark the user as connected
            _isConnected = true;
        }

        return LoginRequest.OK;
    }

    public SimpleResponse TryLogout()
    {
        synchronized (this)
        {
            // check if the user is already logged out and set the connected flag to false
            if (!_isConnected) { return LogoutRequest.USER_NOT_LOGGED; }
            _isConnected = false;
        }

        return LogoutRequest.OK;
    }

    public static boolean IsUsernameValid(String username) { return username.length() >= 3; }
    public static boolean IsPasswordValid(String password) { return password.length() >= 3; }

    public static boolean Exists(String name) { return _users.containsKey(name); }

    public static User FromName(String name) throws UserNotRegisteredException
    {
        User user = _users.get(name);
        if (user == null) { throw new UserNotRegisteredException(); }
        return user;
    }

    public static void Save()
    {
        File file = new File(SAVE_FILE);

        try(FileWriter fileWriter = new FileWriter(file);
            JsonWriter jsonWriter = new JsonWriter(fileWriter))
        {
            jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
            jsonWriter.beginArray();

            Set<Map.Entry<String, User>> entrySet = _users.entrySet();
            for (Map.Entry<String, User> entry : entrySet) {
                jsonWriter.beginObject();
                jsonWriter.name("name").value(entry.getValue()._username);
                jsonWriter.name("password").value(entry.getValue()._password);
                jsonWriter.endObject();
            }

            jsonWriter.endArray();
        }
        catch (IOException e) { throw new RuntimeException(e); }
    }
}
