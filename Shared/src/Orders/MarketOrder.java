
package Orders;

import Messages.MarketOrderRequest;
import Users.User;

/**
 * Represents a market order, a type of order where the price is not fixed.
 * The market order will be executed at the best available price in the market.
 */
public class MarketOrder extends Order
{
    /**
     * Constructor for creating a MarketOrder.
     *
     * @param id     The unique ID for this order.
     * @param method The method for this order (either BID or ASK).
     * @param size   The size (quantity) of the order.
     * @param user   The user who placed the order.
     */
    public MarketOrder(long id, Method method, long size, User user)
    {
        super(id, Type.MARKET, method, size, Long.MAX_VALUE, user);
    }

    /**
     * Creates a MarketOrder from a MarketOrderRequest.
     *
     * @param orderID The unique order ID.
     * @param request The MarketOrderRequest containing the order details.
     * @param user    The user who placed the order.
     * @return A new MarketOrder instance.
     */
    public static MarketOrder FromRequest(long orderID, MarketOrderRequest request, User user)
    {
        return new MarketOrder(orderID, request.GetMethod(), request.GetSize(), user);
    }
}
