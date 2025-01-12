
package Orders;

import Users.User;

public abstract class Order
{
    private final long _id;
    public final Type _type;
    private final Method _method;
    private long _size;
    private final long _price;
    private final long _timestamp;
    private final User _user;

    public Order(long id, Type type, Method method, long size, long price, User user)
    {
        _id = id;
        _type = type;
        _method = method;
        _size = size;
        _price = price;
        _timestamp = System.currentTimeMillis();
        _user = user;
    }

    public long GetID() { return _id; }
    public Type GetType() { return _type; }
    public Method GetMethod() { return _method; }
    public long GetSize() { return _size; }
    public long GetPrice() { return _price; }
    public long GetTimestamp() { return _timestamp; }
    public User GetUser() { return _user; }

    public boolean IsConsumed() { return _size == 0; }

    private void DecreaseSize(long size) { _size -= size; }

    public long CanSellTo(Order order)
    {
        if (GetMethod() != Method.ASK) { return 0; }
        if (order.GetMethod() != Method.BID) { return 0; }
        if (GetPrice() > order.GetPrice()) { return 0; }

        return Math.min(GetSize(), order.GetSize());
    }

    public long CanBuyFrom(Order order)
    {
        if (GetMethod() != Method.BID) { return 0; }
        if (order.GetMethod() != Method.ASK) { return 0; }
        if (GetPrice() < order.GetPrice()) { return 0; }

        return Math.min(GetSize(), order.GetSize());
    }

    public boolean TrySellTo(Order order)
    {
        long amount = CanSellTo(order);
        if (amount == 0) { return false; }

        DecreaseSize(amount);
        order.DecreaseSize(amount);

        // HistoryRecord historyRecord = new HistoryRecord(this)

        return true;
    }

    public boolean TryBuyFrom(Order order)
    {
        long amount = CanBuyFrom(order);
        if (amount == 0) { return false; }

        DecreaseSize(amount);
        order.DecreaseSize(amount);

        // HistoryRecord historyRecord = new HistoryRecord(this)

        return true;
    }

    public boolean Equals(Order other) { return _id == other._id; }
}
