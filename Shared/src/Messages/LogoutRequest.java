
package Messages;

import Networking.OperationType;
import Networking.Request;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * This class represents a request to log out a user. It extends the `Request` class
 * and provides methods for serializing and deserializing this request to and from JSON format.
 * This class doesn't require any specific data (like username or password) for logout,
 * and the serialization simply results in an empty JSON object.
 */
public class LogoutRequest extends Request
{
    // Predefined simple responses for various logout error cases
    public static final SimpleResponse OK = new SimpleResponse(100, "OK");
    public static final SimpleResponse USER_NOT_LOGGED = new SimpleResponse(101, "User not logged in");
    public static final SimpleResponse OTHER_ERROR_CASES = new SimpleResponse(101, "Other error cases\n");

    /**
     * Constructor for a LogoutRequest. It calls the parent constructor with the operation type `LOGOUT`.
     */
    public LogoutRequest() { super(OperationType.LOGOUT); }

    /**
     * Serializes the content of this LogoutRequest to a JSON writer.
     * Since the logout request doesn't require any specific content to be serialized,
     * this method doesn't write anything to the JSON writer.
     *
     * @param jsonWriter The JSON writer used to serialize the request content.
     * @throws IOException If an I/O error occurs during the serialization process.
     */
    protected void SerializeContent(JsonWriter jsonWriter) throws IOException { }

    /**
     * Deserializes a LogoutRequest from a JSON reader.
     * The method doesn't require any specific data in the JSON, so it just creates a new
     * instance of LogoutRequest.
     *
     * @param jsonReader The JSON reader to read the request content from.
     * @return A new LogoutRequest instance.
     * @throws IOException If an I/O error occurs during the deserialization process.
     */
    public static LogoutRequest DeserializeContent(JsonReader jsonReader) throws IOException { return new LogoutRequest(); }
}
