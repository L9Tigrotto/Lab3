
package Orders;

import Helpers.Tuple;
import Messages.ClosedTradesNotification;
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

    public boolean WantToSell() { return _method == Method.ASK; }
    public boolean WantToBuy() { return _method == Method.BID; }

    public abstract boolean WantToSellAt(long price);
    public abstract boolean WantToBuyAt(long price);

    protected void DecreaseSize(Tuple<Long, Long> size_price) { _size -= size_price.GetX(); }

    public Cart CreateCart() { return new Cart(this); }

    public Tuple<Long, Long> CanSellTo(Order order)
    {
        long price = Math.min(_price, order.GetPrice());
        long size = Math.min(_size, order.GetSize());

        if (!WantToSellAt(price) || !order.WantToBuyAt(price)) { return new Tuple<>(0L, 0L); }

        return new Tuple<>(size, price);
    }

    public Tuple<Long, Long> CanBuyFrom(Order order)
    {
        long price = Math.min(_price, order.GetPrice());
        long size = Math.min(_size, order.GetSize());

        boolean wantToBuy = WantToBuyAt(price);
        boolean wantToSell = order.WantToSellAt(price);

        if (!WantToBuyAt(price) || !order.WantToSellAt(price)) { return new Tuple<>(0L, 0L); }

        return new Tuple<>(size, price);
    }

    public boolean TrySellTo(Order order, ClosedTradesNotification notification)
    {
        Tuple<Long, Long> size_price = CanSellTo(order);
        if (size_price.GetX() == 0) { return false; }

        DecreaseSize(size_price);
        order.DecreaseSize(size_price);

        if (order.IsConsumed()) { notification.Add(order, size_price); }

        HistoryRecord record = new HistoryRecord(this, size_price);
        HistoryRecordCollection.Add(record);

        return true;
    }

    public boolean TryBuyFrom(Order order, ClosedTradesNotification notification)
    {
        Tuple<Long, Long> size_price = CanBuyFrom(order);
        if (size_price.GetX() == 0) { return false; }

        DecreaseSize(size_price);
        order.DecreaseSize(size_price);

        if (order.IsConsumed()) { notification.Add(order, size_price); }

        HistoryRecord record = new HistoryRecord(this, size_price);
        HistoryRecordCollection.Add(record);

        return true;
    }
}
