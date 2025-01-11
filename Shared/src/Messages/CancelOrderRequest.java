
package Messages;

import Networking.Request;
import Orders.Type;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class CancelOrderRequest extends Request
{
    private final long _orderID;

    public CancelOrderRequest(long orderID)
    {
        super("cancelOrder");
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
        long orderID;
        try { orderID = Long.parseLong(jsonReader.nextString()); }
        catch (NumberFormatException e) { throw new RuntimeException("Invalid orderID from JSON (got " + temp + ")"); }

        return new CancelOrderRequest(orderID);
    }
}
