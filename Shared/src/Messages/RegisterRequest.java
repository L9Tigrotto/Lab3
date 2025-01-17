
package Messages;

import Networking.OperationType;
import Networking.Request;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * This class represents a request to register a new user. It extends the `Request` class
 * and provides methods for serializing and deserializing this request to and from JSON format.
 * A register request includes a username and password for the new user.
 */
public class RegisterRequest extends Request
{
    // response constants for different outcomes of the registration process
    public static final SimpleResponse OK = new SimpleResponse(100, "OK");
    public static final SimpleResponse INVALID_PASSWORD = new SimpleResponse(101, "Invalid password");
    public static final SimpleResponse USERNAME_NOT_AVAILABLE = new SimpleResponse(102, "Username not available");
    public static final SimpleResponse OTHER_ERROR_CASES = new SimpleResponse(103, "Other error cases");

    // the username for the new user
    private final String _username;

    // the password for the new user
    private final String _password;

    /**
     * Constructor to initialize a RegisterRequest with the specified username and password.
     *
     * @param username The username for the new user.
     * @param password The password for the new user.
     */
    public RegisterRequest(String username, String password)
    {
        super(OperationType.REGISTER);
        _username = username;
        _password = password;
    }

    /**
     * Getter for the username for the new user.
     *
     * @return The username for the new user.
     */
    public String GetUsername() { return _username; }

    /**
     * Getter for the password for the new user.
     *
     * @return The password for the new user.
     */
    public String GetPassword() { return _password; }

    /**
     * Serializes the content of this RegisterRequest to a JSON writer.
     * The username and password are written as name-value pairs in the JSON format.
     *
     * @param jsonWriter The JSON writer used to serialize the request content.
     * @throws IOException If an I/O error occurs during the serialization process.
     */
    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("username").value(_username);
        jsonWriter.name("password").value(_password);
    }

    /**
     * Deserializes a RegisterRequest from a JSON reader.
     * The method expects "username" and "password" fields in the JSON input.
     *
     * @param jsonReader The JSON reader to read the request content from.
     * @return A new RegisterRequest instance with the deserialized content.
     * @throws IOException If an I/O error occurs during the deserialization process.
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
