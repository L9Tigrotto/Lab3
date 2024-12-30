
package Messages;

public class OrderResponse
{
    private final int _orderID;

    public OrderResponse(int orderID)
    {
        _orderID = orderID;
    }

    public int GetOrderID() { return _orderID; }

    public String Serialize()
    {
        return "";
    }

    public static SimpleResponse Deserialize()
    {
        return null;
    }
}
