
package Orders;

import Messages.LimitOrderRequest;
import Users.User;

public class LimitOrder extends Order
{
    public LimitOrder(long id, Type type, long size, long limit, User user)
    {
        super(id, type, size, limit, user);
    }

    public long GetLimitPrice() { return GetPrice(); }

    public static LimitOrder FromRequest(long orderID, LimitOrderRequest request, User user)
    {
        return new LimitOrder(orderID, request.GetType(), request.GetSize(), request.GetLimitPrice(), user);
    }
}
