
package Orders;

import Messages.StopOrderRequest;
import Users.User;

/**
 * Represents a stop order, a type of order that becomes a market order when the stop price is reached.
 * The stop price triggers the execution of the order when the market price crosses it.
 */
public class StopOrder extends Order
{
    // the price at which the stop order will be triggered.
    private final long _stopPrice;

    /**
     * Constructor for creating a StopOrder.
     *
     * @param id        The unique ID for this order.
     * @param method    The method for this order (either BID or ASK).
     * @param size      The size (quantity) of the order.
     * @param stopPrice The price at which the stop order is triggered.
     * @param user      The user who placed the order.
     */
    public StopOrder(long id, Method method, long size, long stopPrice, User user)
    {
        super(id, Type.STOP, method, size, Long.MAX_VALUE, user);

        _stopPrice = stopPrice;
    }

    /**
     * Gets the stop price of this order.
     *
     * @return The stop price at which the order will be triggered.
     */
    public long GetStopPrice() { return _stopPrice; }

    /**
     * Creates a StopOrder from a StopOrderRequest.
     *
     * @param orderID The unique order ID.
     * @param request The StopOrderRequest containing the order details.
     * @param user    The user who placed the order.
     * @return A new StopOrder instance.
     */
    public static StopOrder FromRequest(long orderID, StopOrderRequest request, User user)
    {
        return new StopOrder(orderID, request.GetMethod(), request.GetSize(), request.GetStopPrice(), user);
    }
}
