
package Orders;

import Messages.MarketOrderRequest;
import Users.User;

public class MarketOrder extends Order
{
    public MarketOrder(long id, Method method, long size, User user)
    {
        super(id, Type.MARKET, method, size, 0, user);
    }

    public Cart CreateCart() { return new Cart(this); }

    public static MarketOrder FromRequest(long orderID, MarketOrderRequest request, User user)
    {
        return new MarketOrder(orderID, request.GetMethod(), request.GetSize(), user);
    }
}
