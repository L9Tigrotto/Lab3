
package Messages;

import Helpers.Utilities;
import Networking.OperationType;
import Networking.Request;
import Orders.Method;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * This class represents a request to place a stop order. It extends the `Request` class
 * and provides methods for serializing and deserializing this request to and from JSON format.
 * A stop order request contains the method (buy or sell), size, and stop price for the order.
 */
public class StopOrderRequest extends Request
{
    // the method of the stop order (buy or sell)
    private final Method _method;

    // the size of the stop order
    private final long _size;

    // the stop price of the stop order
    private final long _stopPrice;

    /**
     * Constructor to initialize a StopOrderRequest with the specified method, size, and stop price.
     *
     * @param method The method of the stop order (buy or sell).
     * @param size The size of the stop order (number of units).
     * @param stopPrice The stop price at which the order becomes active.
     */
    public StopOrderRequest(Method method, long size, long stopPrice) {
        super(OperationType.INSERT_STOP_ORDER);
        _method = method;
        _size = size;
        _stopPrice = stopPrice;
    }

    /**
     * Getter for the method of the stop order.
     *
     * @return The method (buy or sell) of the stop order.
     */
    public Method GetMethod() { return _method; }

    /**
     * Getter for the size of the stop order.
     *
     * @return The size of the stop order (number of units).
     */
    public long GetSize() { return _size; }

    /**
     * Getter for the stop price of the stop order.
     *
     * @return The stop price at which the order becomes active.
     */
    public long GetStopPrice() { return _stopPrice; }

    /**
     * Serializes the content of this StopOrderRequest to a JSON writer.
     * The method, size, and stop price are written as name-value pairs in the JSON format.
     *
     * @param jsonWriter The JSON writer used to serialize the request content.
     * @throws IOException If an I/O error occurs during the serialization process.
     */
    protected void SerializeContent(JsonWriter jsonWriter) throws IOException {
        jsonWriter.name("type").value(_method.ToString());
        jsonWriter.name("size").value(_size);
        jsonWriter.name("price").value(_stopPrice);
    }

    /**
     * Deserializes a StopOrderRequest from a JSON reader.
     * The method expects "type", "size", and "price" fields in the JSON input.
     *
     * @param jsonReader The JSON reader to read the request content from.
     * @return A new StopOrderRequest instance with the deserialized content.
     * @throws IOException If an I/O error occurs during the deserialization process.
     */
    public static StopOrderRequest DeserializeContent(JsonReader jsonReader) throws IOException
    {
        Method method = Method.FromString(Utilities.ReadString(jsonReader, "type"));
        long size = Utilities.ReadLong(jsonReader, "size");
        long price = Utilities.ReadLong(jsonReader, "price");

        return new StopOrderRequest(method, size, price);
    }
}
