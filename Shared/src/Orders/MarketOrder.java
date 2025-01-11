
package Orders;

import Users.User;

public class MarketOrder extends Order
{
    public MarketOrder(long id, Type type, long size, long time, User user)
    {
        super(id, type, size, 0, time, user);
    }

    public Cart CreateCart() { return new Cart(this); }
}
