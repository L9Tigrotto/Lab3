
package Orders;

import Messages.MarketOrderRequest;
import Users.User;

public class MarketOrder extends Order
{
    public MarketOrder(long id, Type type, long size, User user)
    {
        super(id, type, size, 0, user);
    }

    public Cart CreateCart() { return new Cart(this); }

    public static MarketOrder FromRequest(long orderID, MarketOrderRequest request, User user)
    {
        return new MarketOrder(orderID, request.GetType(), request.GetSize(), user);
    }
}
