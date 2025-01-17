
package Users;

import Helpers.Utilities;
import Messages.LoginRequest;
import Messages.LogoutRequest;
import Messages.RegisterRequest;
import Messages.SimpleResponse;
import Networking.Connection;
import com.google.gson.FormattingStyle;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton class that manages a collection of users, providing functionality
 * for user registration, login, logout, and persistence through file storage.
 */
public class UserCollection
{
    // the singleton instance of UserCollection
    private static final UserCollection _instance = new UserCollection();

    // map to store registered users (username -> User object)
    private final ConcurrentHashMap<String, User> _registered;

    // map to store connected users (username -> Connection object)
    private final ConcurrentHashMap<String, Connection> _connected;

    /**
     * Private constructor to initialize the UserCollection.
     * It creates empty collections for registered and connected users.
     */
    private UserCollection()
    {
        _registered = new ConcurrentHashMap<>();
        _connected = new ConcurrentHashMap<>();
    }

    /**
     * Internal method to check if a user is registered.
     *
     * @param username the username to check
     * @return true if the user is registered, false otherwise
     */
    private boolean IsRegisteredInternal(String username) { return _registered.containsKey(username); }

    /**
     * Internal method to check if a user is currently connected.
     *
     * @param username the username to check
     * @return true if the user is connected, false otherwise
     */
    private boolean IsConnectedInternal(String username) { return _connected.containsKey(username); }


    /**
     * Internal method to get a user by their username. Throws an exception if the user is not registered.
     *
     * @param username the username of the user to retrieve
     * @return the User object associated with the username
     * @throws UserNotRegisteredException if the user is not registered
     */
    private User FromNameInternal(String username) throws UserNotRegisteredException
    {
        User user = _registered.get(username);
        if (user == null) { throw new UserNotRegisteredException(); }
        return user;
    }

    /**
     * Internal method to try registering a new user.
     *
     * @param username the username of the new user
     * @param password the password of the new user
     * @return a response indicating the result of the registration attempt
     */
    private SimpleResponse TryRegisterInternal(String username, String password)
    {
        // attempt to insert the new user into the collection
        User user = new User(username, password);

        synchronized(_registered)
        {
            if (_registered.putIfAbsent(username, user) != null) { return RegisterRequest.USERNAME_NOT_AVAILABLE; }
        }

        // if the user is successfully inserted inserted, send an OK response
        return RegisterRequest.OK;
    }

    /**
     * Internal method to handle user login attempts.
     *
     * @param user the user attempting to log in
     * @param password the password provided by the user
     * @param connection the connection associated with the login attempt
     * @return a response indicating the result of the login attempt
     */
    public SimpleResponse TryLoginInternal(User user, String password, Connection connection)
    {
        // check if the password matches
        if (!user.MatchPassword(password)) { return LoginRequest.USERNAME_PASSWORD_MISMATCH; }

        // attempt to add the user to the list of connected users (ensures only one connection per user)
        if (_connected.putIfAbsent(user.GetUsername(), connection) == null) { return LoginRequest.OK; }
        else { return LoginRequest.USERNAME_PASSWORD_MISMATCH; }
    }

    /**
     * Internal method to handle user logout attempts.
     *
     * @param user the user attempting to log out
     * @return a response indicating the result of the logout attempt
     */
    public SimpleResponse TryLogoutInternal(User user)
    {
        // attempt to remove the user from the list of connected users
        if (_connected.remove(user.GetUsername()) == null) { return LogoutRequest.USER_NOT_LOGGED; }
        return LogoutRequest.OK;
    }

    /**
     * Internal method to load registered users from a file.
     *
     * @param filename the file from which to load user data
     * @throws IOException if an error occurs while reading the file
     */
    private void LoadInternal(String filename) throws IOException
    {
        File userFile = new File(filename);
        if (!userFile.exists()) { return; }

        // open the file and read the user data in JSON format
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

                // avoid overriding duplicate users (shouldn't happen if the data is correct)
                _registered.putIfAbsent(name, new User(name, password));

                jsonReader.endObject();
            }

            jsonReader.endArray();
        }
    }

    /**
     * Internal method to save the current registered users to a file.
     *
     * @param filename the file to which to save user data
     * @throws IOException if an error occurs while writing the file
     */
    private void SaveInternal(String filename) throws IOException
    {
        File userFile = new File(filename);
        if (!userFile.exists()) { return; }

        // open the file and write the user data in JSON format
        try (FileWriter fileWriter = new FileWriter(userFile);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             JsonWriter jsonWriter = new JsonWriter(bufferedWriter))
        {
            jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
            jsonWriter.beginArray();

            synchronized (_registered)
            {
                Set<Map.Entry<String, User>> entrySet = _registered.entrySet();

                //iterate through the registered users and write each user's data to the file
                for (Map.Entry<String, User> entry : entrySet) {
                    jsonWriter.beginObject();
                    jsonWriter.name("name").value(entry.getValue().GetUsername());
                    jsonWriter.name("password").value(entry.getValue().GetPassword());
                    jsonWriter.endObject();
                }
            }

            jsonWriter.endArray();
        }
    }

    // Public methods to interact with the UserCollection

    /**
     * Checks if a user is registered by their username.
     *
     * @param username the username to check
     * @return true if the user is registered, false otherwise
     */
    public static boolean IsRegistered(String username) { return _instance.IsRegisteredInternal(username); }

    /**
     * Checks if a user is currently connected by their username.
     *
     * @param username the username to check
     * @return true if the user is connected, false otherwise
     */
    public static boolean IsConnected(String username) { return _instance.IsConnectedInternal(username); }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return the User object associated with the username
     * @throws UserNotRegisteredException if the user is not registered
     */
    public static User FromName(String username) throws UserNotRegisteredException { return _instance.FromNameInternal(username); }

    /**
     * Attempts to register a new user.
     *
     * @param username the username of the new user
     * @param password the password of the new user
     * @return a response indicating the result of the registration attempt
     */
    public static SimpleResponse TryRegister(String username, String password) { return _instance.TryRegisterInternal(username, password); }

    /**
     * Attempts to log in a user.
     *
     * @param user the user attempting to log in
     * @param password the password provided by the user
     * @param connection the connection associated with the login attempt
     * @return a response indicating the result of the login attempt
     */
    public static SimpleResponse TryLogin(User user, String password, Connection connection) { return _instance.TryLoginInternal(user, password, connection); }

    /**
     * Attempts to log out a user.
     *
     * @param user the user attempting to log out
     * @return a response indicating the result of the logout attempt
     */
    public static SimpleResponse TryLogout(User user) { return _instance.TryLogoutInternal(user); }

    /**
     * Loads the registered users from a file.
     *
     * @param filename the file from which to load user data
     * @throws IOException if an error occurs while reading the file
     */
    public static void Load(String filename) throws IOException { _instance.LoadInternal(filename); }

    /**
     * Saves the registered users to a file.
     *
     * @param filename the file to which to save user data
     * @throws IOException if an error occurs while writing the file
     */
    public static void Save(String filename) throws IOException { _instance.SaveInternal(filename); }
}
