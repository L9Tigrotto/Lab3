
package Orders;

import Users.User;

public class LimitOrder extends Order
{
    public LimitOrder(long id, Type type, long size, long limit, long time, User user)
    {
        super(id, type, size, limit, time, user);
    }

    public long GetLimitPrice() { return GetSize(); }

    public long CanSellTo(Order order)
    {
        if (GetType() != Type.BID) { return 0; }
        if (order.GetType() != Type.ASK) { return 0; }
        if (order.GetPrice() < GetLimitPrice()) { return 0; }

        return Math.min(GetSize(), order.GetSize());
    }

    public long CanBuyFrom(Order order)
    {
        if (GetType() != Type.ASK) { return 0; }
        if (order.GetType() != Type.BID) { return 0; }
        if (GetLimitPrice() < order.GetPrice()) { return 0; }

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
}
