
package Orders;

import Users.User;

public class LimitOrder extends Order
{
    public LimitOrder(long id, Type type, long size, long limit, long time, User user)
    {
        super(id, type, size, limit, time, user);
    }

    public long GetLimitPrice() { return GetPrice(); }
}
