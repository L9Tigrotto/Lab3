
package Orders;

import Users.User;

public class MarketOrder extends Order
{
    public MarketOrder(long id, Type type, long size, long time, User user)
    {
        super(id, type, size, 0, time, user);
    }

    public long GetLimit() { return GetSize(); }

    public long CanSellTo(Order order)
    {
        if (GetType() != Type.ASK) { return 0; }
        if (order.GetType() != Type.BID) { return 0; }

        return Math.min(GetSize(), order.GetSize());
    }

    public long CanBuyFrom(Order order)
    {
        if (GetType() != Type.BID) { return 0; }
        if (order.GetType() != Type.ASK) { return 0; }

        return Math.min(GetSize(), order.GetSize());
    }

    public boolean TrySellTo(Order order)
    {
        if (GetType() != Type.ASK) { return false; }
        if (order.GetType() != Type.BID) { return false; }

        long soldAmount = Math.min(GetSize(), order.GetSize());
        ChangeSize(soldAmount);
        order.ChangeSize(soldAmount);
        return true;
    }

    public boolean TryBuyFrom(Order order)
    {
        if (GetType() != Type.BID) { return false; }
        if (order.GetType() != Type.ASK) { return false; }

        long soldAmount = Math.min(GetSize(), order.GetSize());
        ChangeSize(soldAmount);
        order.ChangeSize(soldAmount);
        return true;
    }
}
