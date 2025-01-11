
package Messages;

import Networking.Response;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class OrderResponse extends Response
{
    public static final OrderResponse INVALID = new OrderResponse(-1);
    private final long _orderID;

    public OrderResponse(long orderID) {
        _orderID = orderID;
    }

    public long GetOrderID() { return _orderID; }

    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("orderID").value(_orderID);
    }

    public static OrderResponse FromJson(JsonReader jsonReader) throws IOException
    {
        // read the id directly
        long orderID = jsonReader.nextLong();
        return new OrderResponse(orderID);
    }
}
