
package Networking;

import Messages.*;
import com.google.gson.FormattingStyle;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;

/**
 * This abstract class represents a network request that can be serialized to and deserialized from JSON format.
 * The requests can be used to perform various operations in the system. Each request includes an operation type
 * and specific content that will be serialized into a JSON object.
 */
public abstract class Request
{
    // the operation type of the request
    private final OperationType _operation;

    /**
     * Constructor to create a new request with the specified operation type.
     *
     * @param operation The type of operation this request represents (e.g., REGISTER, LOGIN).
     */
    public Request(OperationType operation) { _operation = operation; }

    /**
     * Gets the operation type associated with this request.
     *
     * @return The operation type of the request.
     */
    public OperationType GetOperation() { return _operation; }

    /**
     * Converts the request object into a well-formatted JSON string.
     * The request is serialized into a JSON structure, which includes an "operation" field and a "values" field.
     * The "values" field is populated with the content specific to the type of request.
     *
     * The general structure of the serialized JSON will look like:
     * {
     *   "operation": "<operation_type>",
     *   "values": {
     *     // Specific content serialized by the SerializeContent method
     *   }
     * }
     *
     * Subclasses of this class must implement the `SerializeContent` method to serialize their own specific content
     * into the "values" object within the JSON string.
     *
     * @return A string representing the JSON-formatted request.
     * @throws IOException If an error occurs during the serialization process.
     */
    public String ToJson() throws IOException
    {
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = new JsonWriter(stringWriter);)
        {
            jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
            jsonWriter.beginObject();

            // serialize the operation type as the "operation" field in JSON
            jsonWriter.name("operation").value(_operation.ToString());

            // serialize the specific content of the request under the "values" field
            jsonWriter.name("values");
            jsonWriter.beginObject();
            SerializeContent(jsonWriter);
            jsonWriter.endObject();

            jsonWriter.endObject();
        }

        return stringWriter.toString();
    }

    /**
     * Abstract method that must be implemented by subclasses to serialize the specific content of the request.
     * The content will be serialized inside the "values" field of the JSON structure.
     *
     * @param jsonWriter The JsonWriter object used to write the content into the JSON.
     * @throws IOException If an error occurs during the serialization process.
     */
    protected abstract void SerializeContent(JsonWriter jsonWriter) throws IOException;

    /**
     * Deserializes a JSON string representing a network request and returns the corresponding Request object.
     * This static method reads the JSON string, identifies the operation type, and constructs the corresponding
     * request object by calling the appropriate deserialization method for the specific request type.
     *
     * The general process is:
     * - Read the "operation" field and determine which type of request is being represented.
     * - Based on the operation type, call the deserialization method for that request type.
     *
     * @param json The JSON string representing a network request.
     * @return The deserialized Request object.
     * @throws IOException If an error occurs during JSON parsing or deserialization.
     * @throws ParseException If an error occurs during parsing the JSON.
     */
    public static Request FromJson(String json) throws IOException, ParseException
    {
        String temp;
        Request request;

        try (StringReader stringReader = new StringReader(json);
            JsonReader jsonReader = new JsonReader(stringReader))
        {
            jsonReader.beginObject();

            // Read the "operation" field to determine the type of request
            temp = jsonReader.nextName();
            if (!temp.equals("operation")) { throw new IOException("Expected 'operation' name from JSON (got " + temp + ")"); }
            OperationType operation = OperationType.FromString(jsonReader.nextString());
            if (operation == null) { throw new IOException("Invalid operation from JSON (got " + temp + ")"); }

            // read the "values" field that contains the specific content for the request
            temp = jsonReader.nextName();
            if (!temp.equals("values")) { throw new IOException("Expected 'values' name from JSON (got " + temp + ")"); }
            jsonReader.beginObject();

            // handle different request types based on the operation field
            switch (operation) {
                case REGISTER -> request = RegisterRequest.DeserializeContent(jsonReader);
                case UPDATE_CREDENTIALS -> request = UpdateCredentialsRequest.DeserializeContent(jsonReader);
                case LOGIN -> request = LoginRequest.DeserializeContent(jsonReader);
                case LOGOUT -> request = LogoutRequest.DeserializeContent(jsonReader);
                case INSERT_MARKET_ORDER -> request = MarketOrderRequest.DeserializeContent(jsonReader);
                case INSERT_LIMIT_ORDER -> request = LimitOrderRequest.DeserializeContent(jsonReader);
                case INSERT_STOP_ORDER -> request = StopOrderRequest.DeserializeContent(jsonReader);
                case CANCEL_ORDER -> request = CancelOrderRequest.DeserializeContent(jsonReader);
                case GET_PRICE_HISTORY -> request = GetPriceHistoryRequest.DeserializeContent(jsonReader);
                default -> throw new IOException("Invalid operation from JSON (got " + temp + ")");
            }

            jsonReader.endObject(); // end of "values" object
            jsonReader.endObject(); // end of main JSON object
        }

        return request;
    }
}
