
package Orders;

import Users.User;

public class Order
{
    private static int NextID = 0;

    static
    {

    }


    private final long _id;
    private final long _price;
    private long _size;
    private long _time;
    private final User _user;

    public Order(long price, long size)
    {
        _id = NextID++;
        _price = price;
        _size = size;
        _user = null;
    }

    public long GetPrice() { return _price; }
    public long GetTimestamp() { return _time; }
}
