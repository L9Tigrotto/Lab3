
package Messages;

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

    public Method GetType() { return _method; }
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
        // read the "type" field
        String temp = jsonReader.nextName();
        if (!temp.equals("type")) { throw new IOException("Supposed to read 'type' from JSON (got " + temp + ")"); }
        Method type = Method.FromString(jsonReader.nextString());
        if (type == null) { throw new IOException("Invalid type from JSON (got " + temp + ")"); }

        // read the "size" field
        temp = jsonReader.nextName();
        if (!temp.equals("size")) { throw new IOException("Supposed to read 'size' from JSON (got " + temp + ")"); }
        long size;
        try { size = Long.parseLong(jsonReader.nextString()); }
        catch (NumberFormatException e) { throw new IOException("Invalid size from JSON (got " + temp + ")"); }

        // read the "price" field
        temp = jsonReader.nextName();
        if (!temp.equals("price")) { throw new IOException("Supposed to read 'price' from JSON (got " + temp + ")"); }
        long price;
        try { price = Long.parseLong(jsonReader.nextString()); }
        catch (NumberFormatException e) { throw new IOException("Invalid price from JSON (got " + temp + ")"); }

        return new LimitOrderRequest(type, size, price);
    }
}
