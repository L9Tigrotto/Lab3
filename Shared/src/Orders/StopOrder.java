
package Orders;

import Users.User;

public class StopOrder extends Order
{
    public StopOrder(long id, Type type, long size, long limit, long time, User user)
    {
        super(id, type, size, limit, time, user);
    }
}
