
package Orders;

public class HistoryRecord
{
    private final long _orderID;
    private final Method _type;
    private final Type _orderType;
    private final long _size;
    private final long _price;
    private final long _timestamp;

    public HistoryRecord(long orderID, Method method, Type type, long size, long price, long timestamp)
    {
        _orderID = orderID;
        _type = method;
        _orderType = type;
        _size = size;
        _price = price;
        _timestamp = timestamp;
    }

    public HistoryRecord(Order order, long size, long price)
    {
        _orderID = order.GetID();
        _type = order.GetMethod();
        _orderType = order.GetType();
        _size = size;
        _price = price;
        _timestamp = System.currentTimeMillis();
    }
}
