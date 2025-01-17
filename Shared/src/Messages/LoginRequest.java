
package Messages;

import Helpers.Utilities;
import Networking.OperationType;
import Networking.Request;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * This class represents a login request, containing the user's username and password.
 * It extends the `Request` class and provides methods for serializing and deserializing
 * the request data to and from JSON format. It also defines several predefined response
 * statuses for different error cases during the login process.
 */
public class LoginRequest extends Request
{
    // predefined simple responses for various login error cases
    public static final SimpleResponse OK = new SimpleResponse(100, "OK");
    public static final SimpleResponse USERNAME_PASSWORD_MISMATCH = new SimpleResponse(101, "Username/Password mismatch or non existent user");
    public static final SimpleResponse NON_EXISTENT_USER = new SimpleResponse(101, "Username/Password mismatch or non existent user");
    public static final SimpleResponse USER_ALREADY_LOGGED_IN = new SimpleResponse(102, "User already logged in");
    public static final SimpleResponse OTHER_ERROR_CASES = new SimpleResponse(103, "Other error cases");

    // the username of the user attempting to log in
    private final String _username;

    // the password of the user attempting to log in
    private final String _password;

    /**
     * Constructor to initialize a LoginRequest with a username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     */
    public LoginRequest(String username, String password)
    {
        super(OperationType.LOGIN);
        _username = username;
        _password = password;
    }

    /**
     * Getter for the username of the login request.
     *
     * @return The username of the user.
     */
    public String GetUsername() { return _username; }

    /**
     * Getter for the password of the login request.
     *
     * @return The password of the user.
     */
    public String GetPassword() { return _password; }

    /**
     * Serializes the content of this LoginRequest to a JSON writer.
     * The username and password are written as name-value pairs in the JSON format.
     *
     * @param jsonWriter The JSON writer to serialize the content to.
     * @throws IOException If an I/O error occurs during the serialization process.
     */
    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("username").value(_username);
        jsonWriter.name("password").value(_password);
    }

    /**
     * Deserializes a LoginRequest from a JSON reader.
     * The method expects "username" and "password" fields in the JSON input.
     *
     * @param jsonReader The JSON reader to read the request content from.
     * @return A new LoginRequest instance with the deserialized content.
     * @throws IOException If an I/O error occurs during the deserialization process.
     */
    public static LoginRequest DeserializeContent(JsonReader jsonReader) throws IOException
    {
        String username = Utilities.ReadString(jsonReader, "username");
        String password = Utilities.ReadString(jsonReader, "password");

        return new LoginRequest(username, password);
    }
}
