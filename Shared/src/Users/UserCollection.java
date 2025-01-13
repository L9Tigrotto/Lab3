
package Users;

import Helpers.Utilities;
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

    private final ConcurrentHashMap<String, User>_collection;

    private UserCollection() { _collection = new ConcurrentHashMap<>(); }

    private boolean ExistsInternal(String username) { return _collection.containsKey(username); }

    private User FromNameInternal(String username) throws UserNotRegisteredException
    {
        User user = _collection.get(username);
        if (user == null) { throw new UserNotRegisteredException(); }
        return user;
    }

    private SimpleResponse TryRegisterInternal(String username, String password)
    {
        // attempt to insert the new user into the collection. Handle the case where the username is already taken,
        // even though the initial check might have missed it. This could happen due to race conditions
        User user = new User(username, password);
        if (_collection.putIfAbsent(username, user) != null) { return RegisterRequest.USERNAME_NOT_AVAILABLE; }

        // if all checks pass and the user is successfully inserted, send an OK response
        return RegisterRequest.OK;
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
                _collection.putIfAbsent(name, new User(name, password));

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

            Set<Map.Entry<String, User>> entrySet = _collection.entrySet();

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


    public static boolean Exists(String username) { return _instance.ExistsInternal(username); }
    public static User FromName(String username) throws UserNotRegisteredException { return _instance.FromNameInternal(username); }
    public static SimpleResponse TryRegister(String username, String password) { return _instance.TryRegisterInternal(username, password); }

    public static void Load(String filename) throws IOException { _instance.LoadInternal(filename); }
    public static void Save(String filename) throws IOException { _instance.SaveInternal(filename); }
}
