
package Orders;

import Helpers.Tuple;

public class HistoryRecord
{
    private final long _orderID;
    private final Method _method;
    private final Type _type;
    private final long _size;
    private final long _price;
    private final long _timestamp;

    public HistoryRecord(long orderID, Method method, Type type, long size, long price, long timestamp)
    {
        _orderID = orderID;
        _method = method;
        _type = type;
        _size = size;
        _price = price;
        _timestamp = timestamp;
    }

    public HistoryRecord(Order order, Tuple<Long, Long> size_price)
    {
        _orderID = order.GetID();
        _method = order.GetMethod();
        _type = order.GetType();
        _size = size_price.GetX();
        _price = size_price.GetY();
        _timestamp = System.currentTimeMillis();
    }

    public long GetID() { return _orderID; }
    public Method GetMethod() { return _method; }
    public Type GetType() { return _type; }
    public long GetSize() { return _size; }
    public long GetPrice() { return _price; }
    public long GetTimestamp() { return _timestamp; }
}
