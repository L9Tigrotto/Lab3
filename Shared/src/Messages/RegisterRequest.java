
package Messages;

import Network.Request;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * This class represents a RegisterRequest message sent from the client to the server.
 * It contains the username and password of the user trying to register.
 * The class also defines several predefined responses that the server can send back to the client
 * depending on the outcome of the registration process.
 */
public class RegisterRequest extends Request
{
    public static final SimpleResponse OK_RESPONSE = new SimpleResponse(100, "OK");
    public static final SimpleResponse INVALID_PASSWORD_RESPONSE = new SimpleResponse(101, "Invalid Password");
    public static final SimpleResponse USERNAME_NOT_AVAILABLE_RESPONSE = new SimpleResponse(102, "Username not available");
    public static final SimpleResponse OTHER_ERROR_CASES_RESPONSE = new SimpleResponse(103, "Other error cases");

    private final String _username;
    private final String _password;

    /**
     * Constructs a new RegisterRequest object with the given username and password.
     *
     * @param username The desired username for the new user.
     * @param password The desired password for the new user.
     */
    public RegisterRequest(String username, String password)
    {
        super("register");
        _username = username;
        _password = password;
    }

    public String GetUsername() { return _username; }
    public String GetPassword() { return _password; }

    /**
     * Checks if the provided password meets the minimum length requirement.
     *
     * @return True if the password length is greater than or equal to 3, false otherwise.
     */
    public boolean IsPasswordValid() { return _password.length() >= 3; }

    /**
     * Checks if the provided username meets the minimum length requirement.
     *
     * @return True if the username length is greater than or equal to 3, false otherwise.
     */
    public boolean IsUsernameValid() { return _username.length() >= 3; }

    /**
     * Serializes the request content to the given JsonWriter.
     *
     * @param jsonWriter The JsonWriter to serialize the content to.
     * @throws IOException If an I/O error occurs during serialization.
     */
    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("username").value(_username);
        jsonWriter.name("password").value(_password);
    }

    /**
     * Deserializes a RegisterRequest object from the given JsonReader.
     *
     * @param jsonReader The JsonReader to deserialize the content from.
     * @return The deserialized RegisterRequest object.
     * @throws IOException If an I/O error occurs during deserialization.
     */
    public static RegisterRequest DeserializeContent(JsonReader jsonReader) throws IOException
    {
        // read the "username" field
        String temp = jsonReader.nextName();
        if (!temp.equals("username")) { throw new IOException("Supposed to read 'username' from JSON (got " + temp + ")"); }
        String username = jsonReader.nextString();

        // read the "password" field
        temp = jsonReader.nextName();
        if (!temp.equals("password")) { throw new IOException("Supposed to read 'password' from JSON (got " + temp + ")"); }
        String password = jsonReader.nextString();

        return new RegisterRequest(username, password);
    }
}
