
package Network;

import Messages.RegisterRequest;
import Messages.UpdateCredentialsRequest;
import com.google.gson.FormattingStyle;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * This class defines an abstract base class for network requests in JSON format.
 * It provides a foundation for building different types of network requests
 * that can be serialized and deserialized using JSON.
 */
public abstract class Request
{
    // the operation type of the request (e.g. "register", "login")
    private final String _operation;

    /**
     * Constructor that takes the operation type of the request.
     *
     * @param operation The operation type of the request.
     */
    public Request(String operation) { _operation = operation; }

    /**
     * Gets the operation type of the request.
     *
     * @return The operation type as a string.
     */
    public String GetOperation() { return _operation; }

    /**
     * Converts the request object to a well-formatted JSON string using Gson.
     * This method serializes the request object into a JSON structure with
     * the following format:
     *
     * ```json
     * {
     *   "operation": "<operation_type>",
     *   "values": {
     *     // Specific content serialized by SerializeContent
     *   }
     * }
     * ```
     *
     * Subclasses must implement the `SerializeContent` method to serialize
     * their specific content into the "values" object within the JSON string.
     *
     * @return The JSON string representation of the request object.
     * @throws IOException If an error occurs during JSON serialization.
     */
    public String ToJson() throws IOException
    {
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = new JsonWriter(stringWriter);)
        {
            jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
            jsonWriter.beginObject();

            jsonWriter.name("operation").value(_operation);
            jsonWriter.name("values");
            jsonWriter.beginObject();
            SerializeContent(jsonWriter);
            jsonWriter.endObject();

            jsonWriter.endObject();
        }

        return stringWriter.toString();
    }

    /**
     * Abstract method that subclasses must implement to serialize their
     * specific content into the JSON object under the "values" key.
     *
     * @param jsonWriter The JsonWriter object used for serialization.
     * @throws IOException If an error occurs during JSON serialization.
     */
    protected abstract void SerializeContent(JsonWriter jsonWriter) throws IOException;

    /**
     * Parses a JSON string representing a network request and returns a
     * corresponding Request object. This static method deserializes the JSON
     * string based on the "operation" type specified in the JSON.
     *
     * @param json The JSON string representing the network request.
     * @return The Request object corresponding to the JSON string.
     * @throws IOException If an error occurs during JSON deserialization.
     */
    public static Request FromJson(String json) throws IOException
    {
        String temp;
        String operation;
        Request request;

        try (StringReader stringReader = new StringReader(json);
            JsonReader jsonReader = new JsonReader(stringReader))
        {
            jsonReader.beginObject();

            // read the "operation" field
            temp = jsonReader.nextName();
            if (!temp.equals("operation")) { throw new IOException("Supposed to read 'operation' name from JSON (got " + temp + ")"); }
            operation = jsonReader.nextString();

            // read the "values" field
            temp = jsonReader.nextName();
            if (!temp.equals("values")) { throw new IOException("Supposed to read 'values' name from JSON (got " + temp + ")"); }
            jsonReader.beginObject();

            // handle different operation types
            switch (operation)
            {
                case "register" -> request = RegisterRequest.DeserializeContent(jsonReader);
                case "updateCredentials" -> request = UpdateCredentialsRequest.DeserializeContent(jsonReader);
                default -> throw new IOException("Supposed to read a valid transmittable name from JSON (got " + temp + ")");
            }

            jsonReader.endObject(); // end of "values" object
            jsonReader.endObject(); // end of main JSON object
        }

        return request;
    }
}
