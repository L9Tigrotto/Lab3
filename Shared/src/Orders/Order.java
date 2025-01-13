
package Orders;

import Helpers.Tuple;
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

    public Tuple<Long, Long> CanSellTo(Order order)
    {
        if (GetMethod() != Method.ASK) { return new Tuple<>(0L, 0L); }
        if (order.GetMethod() != Method.BID) { return new Tuple<>(0L, 0L); }
        if (GetPrice() > order.GetPrice()) { return new Tuple<>(0L, 0L); }

        return new Tuple<>(Math.min(GetSize(), order.GetSize()), GetPrice());
    }

    public Tuple<Long, Long> CanBuyFrom(Order order)
    {
        if (GetMethod() != Method.BID) { return new Tuple<>(0L, 0L); }
        if (order.GetMethod() != Method.ASK) { return new Tuple<>(0L, 0L); }
        if (GetPrice() < order.GetPrice()) { return new Tuple<>(0L, 0L); }

        return new Tuple<>(Math.min(GetSize(), order.GetSize()), GetPrice());
    }

    public boolean TrySellTo(Order order)
    {
        Tuple<Long, Long> size_price = CanSellTo(order);
        if (size_price.GetX() == 0) { return false; }

        DecreaseSize(size_price.GetX());
        order.DecreaseSize(size_price.GetX());

        HistoryRecord record = new HistoryRecord(this, size_price);
        HistoryRecordCollection.Add(record);

        return true;
    }

    public boolean TryBuyFrom(Order order)
    {
        Tuple<Long, Long> size_price = CanBuyFrom(order);
        if (size_price.GetX() == 0) { return false; }

        DecreaseSize(size_price.GetX());
        order.DecreaseSize(size_price.GetX());

        HistoryRecord record = new HistoryRecord(this, size_price);
        HistoryRecordCollection.Add(record);

        return true;
    }
}
