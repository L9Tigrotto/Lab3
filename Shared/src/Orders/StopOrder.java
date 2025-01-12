
package Orders;

import Messages.StopOrderRequest;
import Users.User;

public class StopOrder extends Order
{
    public StopOrder(long id, Type type, long size, long stopPrice, User user)
    {
        super(id, type, size, stopPrice, user);
    }

    public static StopOrder FromRequest(long orderID, StopOrderRequest request, User user)
    {
        return new StopOrder(orderID, request.GetType(), request.GetSize(), request.GetStopPrice(), user);
    }
}
