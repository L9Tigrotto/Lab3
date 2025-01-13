
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
 * This class defines an abstract base class for network requests in JSON format.
 * It provides a foundation for building different types of network requests
 * that can be serialized and deserialized using JSON.
 */
public abstract class Request
{
    // the operation type of the request
    private final OperationType _operation;

    /**
     * Constructor that takes the operation type of the request.
     *
     * @param operation The operation type of the request.
     */
    public Request(OperationType operation) { _operation = operation; }

    public OperationType GetOperation() { return _operation; }

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

            jsonWriter.name("operation").value(_operation.ToString());
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
    public static Request FromJson(String json) throws IOException, ParseException
    {
        String temp;
        Request request;

        try (StringReader stringReader = new StringReader(json);
            JsonReader jsonReader = new JsonReader(stringReader))
        {
            jsonReader.beginObject();

            // read the "operation" field
            temp = jsonReader.nextName();
            if (!temp.equals("operation")) { throw new IOException("Supposed to read 'operation' name from JSON (got " + temp + ")"); }
            OperationType operation = OperationType.FromString(jsonReader.nextString());
            if (operation == null) { throw new IOException("Invalid operation from JSON (got " + temp + ")"); }

            // read the "values" field
            temp = jsonReader.nextName();
            if (!temp.equals("values")) { throw new IOException("Supposed to read 'values' name from JSON (got " + temp + ")"); }
            jsonReader.beginObject();

            // handle different operation types
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
