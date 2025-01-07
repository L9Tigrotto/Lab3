
package Users;

import com.google.gson.FormattingStyle;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public boolean ArePasswordEquals(String newPassword) { return _password.equals(newPassword); }
    public boolean IsConnected() { return _isConnected; }

    public void ChangePassword(String newPassword) { _password = newPassword; }

    public static boolean IsUsernameValid(String username) { return username.length() >= 3; }
    public static boolean IsPasswordValid(String password) { return password.length() >= 3; }

    public static boolean Exists(String name) { return _users.containsKey(name); }

    public static synchronized void Insert(String username, String password) throws DuplicateUserException
    {
        User user = new User(username, password);
        if (_users.putIfAbsent(username, user) != null) { throw new DuplicateUserException(""); }
    }

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
