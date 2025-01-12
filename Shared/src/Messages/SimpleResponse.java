
package Messages;

import Helpers.Utilities;
import Networking.Response;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Represents a simple response message sent from the server to the client.
 *
 * This class encapsulates a basic response structure, containing:
 *  - A numeric response code indicating the success or failure of the operation.
 *  - An error message providing more details about the response.
 */
public class SimpleResponse extends Response
{
    // the numeric response code.
    private final int _response;

    // the optional error message.
    private final String _errorMessage;

    /**
     * Constructs a new SimpleResponse object with the given response code and error message.
     *
     * @param response The numeric response code.
     * @param errorMessage The error message (can be null).
     */
    public SimpleResponse(int response, String errorMessage)
    {
        super();
        _response = response;
        _errorMessage = errorMessage;
    }

    public int GetResponse() { return _response; }
    public String GetErrorMessage() { return _errorMessage; }

    /**
     * Serializes the response content to the given JsonWriter.
     *
     * @param jsonWriter The JsonWriter to serialize the content to.
     * @throws IOException If an I/O error occurs during serialization.
     */
    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("response").value(_response);
        jsonWriter.name("errorMessage").value(_errorMessage);
    }

    /**
     * Deserializes a SimpleResponse object from the given JsonReader.
     *
     * **Note:** This method assumes that the `nextName()` call has already been
     * performed by the parent `Response` class to determine the response type.
     *
     * @param jsonReader The JsonReader to deserialize the content from.
     * @return The deserialized SimpleResponse object.
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
