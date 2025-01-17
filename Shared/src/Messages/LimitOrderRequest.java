
package Messages;

import Helpers.Utilities;
import Networking.OperationType;
import Networking.Request;
import Orders.Method;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;


/**
 * This class represents a request to insert a limit order, including the order's method, size, and limit price.
 * It extends the `Request` class and provides methods for serializing and deserializing this request to/from JSON format.
 */
public class LimitOrderRequest extends Request
{
    // the method (buy/sell) of the limit order
    private final Method _method;

    // the size of the limit order
    private final long _size;

    // the price at which the limit order is placed
    private final long _limitPrice;

    /**
     * Constructor that initializes the limit order request with method, size, and limit price.
     *
     * @param method The method (buy or sell) for the order.
     * @param size The size (quantity) of the order.
     * @param limitPrice The price at which the order should be executed.
     */
    public LimitOrderRequest(Method method, long size, long limitPrice)
    {
        super(OperationType.INSERT_LIMIT_ORDER);
        _method = method;
        _size = size;
        _limitPrice = limitPrice;
    }

    /**
     * Getter for the method (buy/sell) of the order.
     *
     * @return The method of the order.
     */
    public Method GetMethod() { return _method; }

    /**
     * Getter for the size of the order.
     *
     * @return The size of the order.
     */
    public long GetSize() { return _size; }

    /**
     * Getter for the limit price of the order.
     *
     * @return The limit price at which the order is placed.
     */
    public long GetLimitPrice() { return _limitPrice; }

    /**
     * Serializes the content of this LimitOrderRequest to a JSON writer.
     *
     * @param jsonWriter The JSON writer used to serialize the request content.
     * @throws IOException If an I/O error occurs during the writing process.
     */
    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("type").value(_method.ToString());
        jsonWriter.name("size").value(_size);
        jsonWriter.name("price").value(_limitPrice);
    }

    /**
     * Deserializes a LimitOrderRequest from a JSON reader.
     *
     * @param jsonReader The JSON reader to read the request content from.
     * @return A new LimitOrderRequest instance with the deserialized content.
     * @throws IOException If an I/O error occurs during reading the JSON content.
     */
    public static LimitOrderRequest DeserializeContent(JsonReader jsonReader) throws IOException
    {
        Method method = Method.FromString(Utilities.ReadString(jsonReader, "type"));
        long size = Utilities.ReadLong(jsonReader, "size");
        long price = Utilities.ReadLong(jsonReader, "price");

        return new LimitOrderRequest(method, size, price);
    }
}
