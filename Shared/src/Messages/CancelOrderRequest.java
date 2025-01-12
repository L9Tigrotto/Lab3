
package Messages;

import Networking.OperationType;
import Networking.Request;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class CancelOrderRequest extends Request
{
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

    private final long _orderID;

    public CancelOrderRequest(long orderID)
    {
        super(OperationType.CANCEL_ORDER);
        _orderID = orderID;
    }

    public long GetOrderID() { return _orderID; }

    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("orderID").value(_orderID);
    }

    public static CancelOrderRequest DeserializeContent(JsonReader jsonReader) throws IOException
    {
        // read the "orderID" field
        String temp = jsonReader.nextName();
        if (!temp.equals("orderID")) { throw new IOException("Supposed to read 'orderID' from JSON (got " + temp + ")"); }
        long orderID = jsonReader.nextLong();

        return new CancelOrderRequest(orderID);
    }
}
