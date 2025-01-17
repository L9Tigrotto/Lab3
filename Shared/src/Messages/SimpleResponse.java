
package Messages;

import Helpers.Utilities;
import Networking.Response;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * This class represents a simple response that contains a response code and an error message.
 * It extends the `Response` class and provides methods to serialize and deserialize the response data to and from JSON.
 */
public class SimpleResponse extends Response
{
    // the numeric response code (e.g., 100 for success, 101 for error, etc.)
    private final int _response;

    // the error message providing details about the response
    private final String _errorMessage;

    /**
     * Constructor to initialize a SimpleResponse with a response code and an error message.
     *
     * @param response The numeric response code.
     * @param errorMessage The error message describing the response.
     */
    public SimpleResponse(int response, String errorMessage)
    {
        super();
        _response = response;
        _errorMessage = errorMessage;
    }

    /**
     * Getter for the response code.
     *
     * @return The numeric response code (e.g., 100 for OK, 101 for error, etc.).
     */
    public int GetResponse() { return _response; }

    /**
     * Getter for the error message.
     *
     * @return The error message associated with the response.
     */
    public String GetErrorMessage() { return _errorMessage; }

    /**
     * Serializes the content of this SimpleResponse into JSON format.
     * The response code and the error message are serialized as name-value pairs.
     *
     * @param jsonWriter The JSON writer used for serialization.
     * @throws IOException If an I/O error occurs during the serialization.
     */
    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("response").value(_response);
        jsonWriter.name("errorMessage").value(_errorMessage);
    }

    /**
     * Deserializes a SimpleResponse from JSON format.
     * The method expects the response code and error message in the JSON input.
     *
     * @param jsonReader The JSON reader used for deserialization.
     * @return A new SimpleResponse instance with the deserialized data.
     * @throws IOException If an I/O error occurs during deserialization.
     */
    public static SimpleResponse FromJson(JsonReader jsonReader) throws IOException
    {
        // read the response code directly
        int response = jsonReader.nextInt();
        String errorMessage = Utilities.ReadString(jsonReader, "errorMessage");

        return new SimpleResponse(response, errorMessage);
    }
}
