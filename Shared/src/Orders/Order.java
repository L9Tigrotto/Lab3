
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

    public boolean IsConsumed() { return _size == 0; }

    public void ChangeSize(long size) { _size -= size; }

    public long CanSellTo(Order order)
    {
        if (GetType() != Type.ASK) { return 0; }
        if (order.GetType() != Type.BID) { return 0; }
        if (GetPrice() > order.GetPrice()) { return 0; }

        return Math.min(GetSize(), order.GetSize());
    }

    public long CanBuyFrom(Order order)
    {
        if (GetType() != Type.BID) { return 0; }
        if (order.GetType() != Type.ASK) { return 0; }
        if (GetPrice() < order.GetPrice()) { return 0; }

        return Math.min(GetSize(), order.GetSize());
    }

    public boolean TrySellTo(Order order)
    {
        long amount = CanSellTo(order);
        if (amount == 0) { return false; }

        ChangeSize(amount);
        order.ChangeSize(amount);
        return true;
    }

    public boolean TryBuyFrom(Order order)
    {
        long amount = CanBuyFrom(order);
        if (amount == 0) { return false; }

        ChangeSize(amount);
        order.ChangeSize(amount);
        return true;
    }

    public boolean Equals(Order other) { return _id == other._id; }
}
