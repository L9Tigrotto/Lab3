
package Messages;

import Helpers.Tuple;
import Helpers.Utilities;
import Orders.Method;
import Orders.Order;
import Orders.Type;
import com.google.gson.FormattingStyle;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

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

            _jsonWriter.name("notification").value("closedTrades");

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
            _jsonWriter.name("timestamp").value(order.GetTimestamp() / 1000);

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

    public static List<Order> DeserializeContent(String text)
    {
        List<Order> orders = new ArrayList<>();

        try (JsonReader jsonReader = new JsonReader(new StringReader(text)))
        {
            jsonReader.beginObject();

            String temp = Utilities.ReadString(jsonReader, "notification");
            if (!temp.equalsIgnoreCase("closedTrades"))
            {
                System.out.println("[ERROR] Unable to deserialize notification");
                return orders;
            }

            temp = jsonReader.nextName();
            if (!temp.equalsIgnoreCase("trades"))
            {
                System.out.println("[ERROR] Unable to deserialize notification");
                return orders;
            }

            jsonReader.beginArray();

            while (jsonReader.hasNext())
            {
                jsonReader.beginObject();

                long orderID = Utilities.ReadLong(jsonReader, "orderID");
                Method method = Method.FromString(Utilities.ReadString(jsonReader, "type"));
                Type type = Type.FromString(Utilities.ReadString(jsonReader, "orderType"));
                long size = Utilities.ReadLong(jsonReader, "size");
                long price = Utilities.ReadLong(jsonReader, "price");
                long timestamp = Utilities.ReadLong(jsonReader, "timestamp");

                Order order = new Order(orderID, type, method, size, price, timestamp);
                orders.add(order);

                jsonReader.endObject();
            }

            jsonReader.endArray();
            jsonReader.endObject();
        }
        catch (IOException e)
        {
            System.out.printf("[ERROR] Unable to deserialize closed trades notification: %s\n", e.getMessage());
            throw new RuntimeException(e);
        }



        return orders;
    }
}
