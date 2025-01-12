
package Messages;

import Networking.OperationType;
import Networking.Request;
import Orders.Method;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class MarketOrderRequest extends Request
{
    private final Method _type;
    private final long _size;

    public MarketOrderRequest(Method type, long size)
    {
        super(OperationType.INSERT_MARKET_ORDER);
        _type = type;
        _size = size;
    }

    public Method GetType() { return _type; }
    public long GetSize() { return _size; }

    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("type").value(_type.ToString());
        jsonWriter.name("size").value(_size);
    }

    public static MarketOrderRequest DeserializeContent(JsonReader jsonReader) throws IOException
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

        return new MarketOrderRequest(type, size);
    }
}
