
package Messages;

import Networking.OperationType;
import Networking.Request;
import Orders.Type;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class LimitOrderRequest extends Request
{
    private final Type _type;
    private final long _size;
    private final long _price;

    public LimitOrderRequest(Type type, long size, long price)
    {
        super(OperationType.INSERT_LIMIT_ORDER);
        _type = type;
        _size = size;
        _price = price;
    }

    public Type GetType() { return _type; }
    public long GetSize() { return _size; }
    public long GetPrice() { return _price; }

    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("type").value(_type.ToString());
        jsonWriter.name("size").value(_size);
        jsonWriter.name("price").value(_price);
    }

    public static LimitOrderRequest DeserializeContent(JsonReader jsonReader) throws IOException
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
