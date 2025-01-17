
package Messages;

import Helpers.Utilities;
import Networking.OperationType;
import Networking.Request;
import Orders.Method;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * This class represents a request to place a market order. It extends the `Request` class
 * and provides methods for serializing and deserializing this request to and from JSON format.
 * A market order request contains the method (buy or sell) and the size of the order.
 */
public class MarketOrderRequest extends Request
{
    // the method of the order (buy or sell)
    private final Method _method;

    // the size of the market order (number of units)
    private final long _size;

    /**
     * Constructor to initialize a MarketOrderRequest with the specified method and size.
     *
     * @param method The method of the order (buy or sell).
     * @param size The size of the market order.
     */
    public MarketOrderRequest(Method method, long size)
    {
        super(OperationType.INSERT_MARKET_ORDER);
        _method = method;
        _size = size;
    }

    /**
     * Getter for the method of the market order.
     *
     * @return The method (buy or sell) of the market order.
     */
    public Method GetMethod() { return _method; }

    /**
     * Getter for the size of the market order.
     *
     * @return The size of the market order (number of units).
     */
    public long GetSize() { return _size; }

    /**
     * Serializes the content of this MarketOrderRequest to a JSON writer.
     * The method and size are written as name-value pairs in the JSON format.
     *
     * @param jsonWriter The JSON writer used to serialize the request content.
     * @throws IOException If an I/O error occurs during the serialization process.
     */
    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("type").value(_method.ToString());
        jsonWriter.name("size").value(_size);
    }

    /**
     * Deserializes a MarketOrderRequest from a JSON reader.
     * The method expects "type" and "size" fields in the JSON input.
     *
     * @param jsonReader The JSON reader to read the request content from.
     * @return A new MarketOrderRequest instance with the deserialized content.
     * @throws IOException If an I/O error occurs during the deserialization process.
     */
    public static MarketOrderRequest DeserializeContent(JsonReader jsonReader) throws IOException
    {
        Method method = Method.FromString(Utilities.ReadString(jsonReader, "type"));
        long size = Utilities.ReadLong(jsonReader, "size");

        return new MarketOrderRequest(method, size);
    }
}
