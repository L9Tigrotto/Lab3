
package Orders;

import Messages.LimitOrderRequest;
import Users.User;

/**
 * Represents a limit order, a specific type of order where the price is fixed.
 * A LimitOrder can be either a buy order (BID) or a sell order (ASK).
 */
public class LimitOrder extends Order
{
    /**
     * Constructor for creating a LimitOrder.
     *
     * @param id     The unique ID for this order.
     * @param method The method for this order (either BID or ASK).
     * @param size   The size (quantity) of the order.
     * @param limit  The limit price for the order.
     * @param user   The user who placed the order.
     */
    public LimitOrder(long id, Method method, long size, long limit, User user) { super(id, Type.LIMIT, method, size, limit, user); }

    /**
     * Determines if this order wants to sell at the given price.
     * A sell limit order will only sell at a price greater than or equal to the limit price.
     *
     * @param price The price at which to sell.
     * @return true if the order wants to sell at the given price; false otherwise.
     */
    @Override public boolean WantToSellAt(long price) { return WantToSell() && price >= GetPrice(); }

    /**
     * Determines if this order wants to buy at the given price.
     * A buy limit order will only buy at a price less than or equal to the limit price.
     *
     * @param price The price at which to buy.
     * @return true if the order wants to buy at the given price; false otherwise.
     */
    @Override public boolean WantToBuyAt(long price) { return WantToBuy() && price <= GetPrice(); }

    /**
     * Creates a LimitOrder from a LimitOrderRequest.
     *
     * @param orderID The unique order ID.
     * @param request The LimitOrderRequest containing the order details.
     * @param user    The user who placed the order.
     * @return A new LimitOrder instance.
     */
    public static LimitOrder FromRequest(long orderID, LimitOrderRequest request, User user)
    {
        return new LimitOrder(orderID, request.GetMethod(), request.GetSize(), request.GetLimitPrice(), user);
    }
}
