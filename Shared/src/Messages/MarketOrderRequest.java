
package Messages;

import Helpers.Utilities;
import Networking.OperationType;
import Networking.Request;
import Orders.Method;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class MarketOrderRequest extends Request
{
    private final Method _method;
    private final long _size;

    public MarketOrderRequest(Method method, long size)
    {
        super(OperationType.INSERT_MARKET_ORDER);
        _method = method;
        _size = size;
    }

    public Method GetMethod() { return _method; }
    public long GetSize() { return _size; }

    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("type").value(_method.ToString());
        jsonWriter.name("size").value(_size);
    }

    public static MarketOrderRequest DeserializeContent(JsonReader jsonReader) throws IOException
    {
        Method method = Method.FromString(Utilities.ReadString(jsonReader, "type"));
        long size = Utilities.ReadLong(jsonReader, "size");

        return new MarketOrderRequest(method, size);
    }
}
