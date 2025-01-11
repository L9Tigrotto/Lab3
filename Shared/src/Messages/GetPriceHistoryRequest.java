
package Messages;

import Networking.OperationType;
import Networking.Request;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class GetPriceHistoryRequest extends Request
{
    public GetPriceHistoryRequest() { super(OperationType.GET_PRICE_HISTORY); }

    protected void SerializeContent(JsonWriter jsonWriter) throws IOException { }

    public static GetPriceHistoryRequest DeserializeContent(JsonReader jsonReader) throws IOException
    {
        return new GetPriceHistoryRequest();
    }

}
