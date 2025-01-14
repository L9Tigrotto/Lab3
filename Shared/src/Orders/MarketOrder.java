
package Orders;

import Messages.MarketOrderRequest;
import Users.User;

public class MarketOrder extends Order
{
    public MarketOrder(long id, Method method, long size, User user)
    {
        super(id, Type.MARKET, method, size, Long.MAX_VALUE, user);
    }

    public boolean WantToSellAt(long price) { return WantToSell(); }
    public boolean WantToBuyAt(long price) { return WantToBuy(); }

    public static MarketOrder FromRequest(long orderID, MarketOrderRequest request, User user)
    {
        return new MarketOrder(orderID, request.GetMethod(), request.GetSize(), user);
    }

    public static MarketOrder FromStopOrder(StopOrder order)
    {
        // use old id so the user knows what is the id
        return new MarketOrder(order.GetID(), order.GetMethod(), order.GetSize(), order.GetUser());
    }
}
