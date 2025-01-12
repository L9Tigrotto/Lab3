
package Messages;

import Helpers.Utilities;
import Networking.OperationType;
import Networking.Request;
import Orders.Method;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class StopOrderRequest extends Request
{
    private final Method _method;
    private final long _size;
    private final long _stopPrice;

    public StopOrderRequest(Method method, long size, long stopPrice) {
        super(OperationType.INSERT_STOP_ORDER);
        _method = method;
        _size = size;
        _stopPrice = stopPrice;
    }

    public Method GetMethod() { return _method; }
    public long GetSize() { return _size; }
    public long GetStopPrice() { return _stopPrice; }

    protected void SerializeContent(JsonWriter jsonWriter) throws IOException {
        jsonWriter.name("type").value(_method.ToString());
        jsonWriter.name("size").value(_size);
        jsonWriter.name("price").value(_stopPrice);
    }

    public static StopOrderRequest DeserializeContent(JsonReader jsonReader) throws IOException {
        Method method = Method.FromString(Utilities.ReadString(jsonReader, "type"));
        long size = Utilities.ReadLong(jsonReader, "size");
        long price = Utilities.ReadLong(jsonReader, "price");

        return new StopOrderRequest(method, size, price);
    }
}
