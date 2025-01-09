package Messages;

public class OrderResponse
{
    private final long _orderID;

    public OrderResponse(long orderID)
    {
        _orderID = orderID;
    }

    public long GetOrderID() { return _orderID; }
}
