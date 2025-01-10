
package Orders;

import Users.User;

public class Order
{
    private final long _id;
    private final long _size;
    private final long _price;
    private final long _time;
    private final User _user;

    public Order(long id, long size, long price, long time, User user)
    {
        _id = id;
        _size = size;
        _price = price;
        _time = time;
        _user = user;
    }

    public long GetID() { return _id; }
    public long GetSize() { return _size; }
    public long GetPrice() { return _price; }
    public long GetTime() { return _time; }
    public User GetUser() { return _user; }
}
