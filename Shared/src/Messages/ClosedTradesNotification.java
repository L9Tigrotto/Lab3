
package Messages;

import Helpers.Tuple;
import Orders.Order;
import com.google.gson.FormattingStyle;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;

public class ClosedTradesNotification
{ ;
    private final StringWriter _stringWriter;
    private final JsonWriter _jsonWriter;

    public ClosedTradesNotification()
    {
        _stringWriter = new StringWriter();
        _jsonWriter = new JsonWriter(_stringWriter);

        try
        {
            _jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
            _jsonWriter.beginObject();

            _jsonWriter.name("trades");
            _jsonWriter.beginArray();
        }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    public void Add(Order order, Tuple<Long, Long> size_price)
    {
        try
        {
            _jsonWriter.beginObject();

            _jsonWriter.name("orderID").value(order.GetID());
            _jsonWriter.name("type").value(order.GetMethod().ToString());
            _jsonWriter.name("orderType").value(order.GetType().ToString());
            _jsonWriter.name("size").value(size_price.GetX());
            _jsonWriter.name("price").value(size_price.GetY());
            _jsonWriter.name("timestamp").value(System.currentTimeMillis() / 1000);

            _jsonWriter.endObject();
        }
        catch (IOException e) { System.out.println("[ERROR] Unable to create json notification"); }

    }

    public void Terminate()
    {
        try
        {
            _jsonWriter.endArray();
            _jsonWriter.endObject();
        }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    public String ToString() { return _stringWriter.toString(); }

    public void Close()
    {
        try
        {
            _jsonWriter.close();
            _stringWriter.close();
        }
        catch (IOException e) { }
    }
}
