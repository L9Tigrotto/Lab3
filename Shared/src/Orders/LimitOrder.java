
package Orders;

import Messages.LimitOrderRequest;
import Users.User;

public class LimitOrder extends Order
{
    public LimitOrder(long id, Method method, long size, long limit, User user) { super(id, Type.LIMIT, method, size, limit, user); }

    public boolean WantToSellAt(long price) { return WantToSell() && price >= GetPrice(); }
    public boolean WantToBuyAt(long price) { return WantToBuy() && price <= GetPrice(); }

    public static LimitOrder FromRequest(long orderID, LimitOrderRequest request, User user)
    {
        return new LimitOrder(orderID, request.GetMethod(), request.GetSize(), request.GetLimitPrice(), user);
    }
}
