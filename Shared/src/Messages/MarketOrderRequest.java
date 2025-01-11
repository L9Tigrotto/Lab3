
package Messages;

import Networking.Request;
import Orders.Type;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class MarketOrderRequest extends Request
{
    private final Type _type;
    private final long _size;

    public MarketOrderRequest(Type type, long size)
    {
        super("insertMarketOrder");
        _type = type;
        _size = size;
    }

    public Type GetType() { return _type; }
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
        Type type = Type.FromString(jsonReader.nextString());
        if (type == null) { throw new IOException("Invalid type from JSON (got " + temp + ")"); }

        // read the "size" field
        temp = jsonReader.nextName();
        if (!temp.equals("size")) { throw new IOException("Supposed to read 'size' from JSON (got " + temp + ")"); }
        long size;
        try { size = Long.parseLong(jsonReader.nextString()); }
        catch (NumberFormatException e) { throw new RuntimeException("Invalid size from JSON (got " + temp + ")"); }

        return new MarketOrderRequest(type, size);
    }
}
