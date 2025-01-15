
package Orders;

import Messages.StopOrderRequest;
import Users.User;

public class StopOrder extends Order
{
    private final long _stopPrice;

    public StopOrder(long id, Method method, long size, long stopPrice, User user)
    {
        super(id, Type.STOP, method, size, Long.MAX_VALUE, user);

        _stopPrice = stopPrice;
    }

    public long GetStopPrice() { return _stopPrice; }

    public boolean WantToSellAt(long price) { return WantToSell(); }
    public boolean WantToBuyAt(long price) { return WantToBuy(); }

    public static StopOrder FromRequest(long orderID, StopOrderRequest request, User user)
    {
        return new StopOrder(orderID, request.GetMethod(), request.GetSize(), request.GetStopPrice(), user);
    }
}
