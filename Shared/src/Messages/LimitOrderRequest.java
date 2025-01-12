
package Messages;

import Helpers.Utilities;
import Networking.OperationType;
import Networking.Request;
import Orders.Method;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class LimitOrderRequest extends Request
{
    private final Method _method;
    private final long _size;
    private final long _limitPrice;

    public LimitOrderRequest(Method method, long size, long limitPrice)
    {
        super(OperationType.INSERT_LIMIT_ORDER);
        _method = method;
        _size = size;
        _limitPrice = limitPrice;
    }

    public Method GetMethod() { return _method; }
    public long GetSize() { return _size; }
    public long GetLimitPrice() { return _limitPrice; }

    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("type").value(_method.ToString());
        jsonWriter.name("size").value(_size);
        jsonWriter.name("price").value(_limitPrice);
    }

    public static LimitOrderRequest DeserializeContent(JsonReader jsonReader) throws IOException
    {
        Method method = Method.FromString(Utilities.ReadString(jsonReader, "type"));
        long size = Utilities.ReadLong(jsonReader, "size");
        long price = Utilities.ReadLong(jsonReader, "price");

        return new LimitOrderRequest(method, size, price);
    }
}
