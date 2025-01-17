
package Messages;

import Helpers.Utilities;
import Networking.OperationType;
import Networking.Request;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * This class represents a request to cancel an order. It extends the `Request` class and
 * provides serialization and deserialization methods for JSON-based communication.
 */
public class CancelOrderRequest extends Request
{
    // simple responses for various result scenarios of the cancel order operation
    public static final SimpleResponse OK = new SimpleResponse(100, "Ok");

    public static final SimpleResponse ORDER_DOES_NOT_EXISTS = new SimpleResponse(101,
            "Order does not exist or belongs to different user or has already been " +
                    "finalized or other error cases");

    public static final SimpleResponse ORDER_BELONG_TO_DIFFERENT_USER = new SimpleResponse(101,
            "Order does not exist or belongs to different user or has already been " +
                    "finalized or other error cases");

    public static final SimpleResponse OTHER_ERROR_CASES = new SimpleResponse(101,
            "Order does not exist or belongs to different user or has already been " +
                    "finalized or other error cases");

    // the unique identifier for the order to be canceled
    private final long _orderID;

    /**
     * Constructor for creating a CancelOrderRequest with a specific order ID.
     *
     * @param orderID The ID of the order to cancel.
     */
    public CancelOrderRequest(long orderID)
    {
        super(OperationType.CANCEL_ORDER);
        _orderID = orderID;
    }

    /**
     * Getter for the order ID.
     *
     * @return The ID of the order to be canceled.
     */
    public long GetOrderID() { return _orderID; }

    /**
     * Serializes the content of this CancelOrderRequest to a JSON writer.
     *
     * @param jsonWriter The JSON writer to serialize the content to.
     * @throws IOException If an I/O error occurs while writing.
     */
    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("orderID").value(_orderID);
    }

    /**
     * Deserializes a CancelOrderRequest from a JSON reader.
     *
     * @param jsonReader The JSON reader to read the request content from.
     * @return A new CancelOrderRequest instance containing the deserialized data.
     * @throws IOException If an I/O error occurs while reading.
     */
    public static CancelOrderRequest DeserializeContent(JsonReader jsonReader) throws IOException
    {
        long orderID = Utilities.ReadLong(jsonReader, "orderID");
        return new CancelOrderRequest(orderID);
    }
}
