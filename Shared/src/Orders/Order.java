
package Orders;

import Users.User;

public abstract class Order
{
    private final long _id;
    private final Type _type;
    private long _size;
    private final long _price;
    private final long _time;
    private final User _user;

    public Order(long id, Type type, long size, long price, long time, User user)
    {
        _id = id;
        _type = type;
        _size = size;
        _price = price;
        _time = time;
        _user = user;
    }

    public long GetID() { return _id; }
    public Type GetType() { return _type; }
    public long GetSize() { return _size; }
    public long GetPrice() { return _price; }
    public long GetTime() { return _time; }
    public User GetUser() { return _user; }

    public void ChangeSize(long size) { _size -= size; }

    public abstract long CanSellTo(Order order);
    public abstract long CanBuyFrom(Order order);

    public abstract boolean TrySellTo(Order order);
    public abstract boolean TryBuyFrom(Order order);

    public boolean Equals(Order other) { return _id == other._id; }
}
