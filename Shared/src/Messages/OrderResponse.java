
package Messages;

import Networking.Response;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * This class represents a response for an order operation. It extends the `Response` class
 * and encapsulates the order ID returned as part of the response. The response can be serialized
 * and deserialized to/from JSON format.
 */
public class OrderResponse extends Response
{
    // a predefined static response for invalid orders (with orderID -1)
    public static final OrderResponse INVALID = new OrderResponse(-1);

    // the unique order ID associated with this response
    private final long _orderID;

    /**
     * Constructor to initialize an OrderResponse with the given order ID.
     *
     * @param orderID The unique ID of the order associated with this response.
     */
    public OrderResponse(long orderID) {
        _orderID = orderID;
    }

    /**
     * Getter for the order ID associated with this response.
     *
     * @return The order ID.
     */
    public long GetOrderID() { return _orderID; }

    /**
     * Serializes the content of this OrderResponse to a JSON writer.
     * It writes the order ID as a name-value pair in the JSON format.
     *
     * @param jsonWriter The JSON writer used to serialize the response content.
     * @throws IOException If an I/O error occurs during the serialization process.
     */
    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("orderID").value(_orderID);
    }

    /**
     * Deserializes an OrderResponse from a JSON reader.
     * This method expects the order ID to be present as a direct value in the JSON input.
     *
     * @param jsonReader The JSON reader to read the response content from.
     * @return A new OrderResponse instance with the deserialized order ID.
     * @throws IOException If an I/O error occurs during the deserialization process.
     */
    public static OrderResponse FromJson(JsonReader jsonReader) throws IOException
    {
        // read the id directly
        long orderID = jsonReader.nextLong();
        return new OrderResponse(orderID);
    }
}
