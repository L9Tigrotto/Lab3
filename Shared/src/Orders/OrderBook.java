package Orders;

import Messages.OrderResponse;
import Messages.RegisterRequest;

import java.util.List;
import java.util.PriorityQueue;

public class OrderBook
{
    /**
     * Priority Queue for storing ask orders (sell orders).
     *
     * Orders are sorted by price in ascending order. If two orders have the same price, they are
     * sorted by timestamp with the oldest order first.
     */
    @SuppressWarnings("ComparatorCombinators")
    private static final PriorityQueue<Order> _askOrders
            = new PriorityQueue<Order>((orderA, orderB) -> {
                int cmp = Long.compare(orderA.GetPrice(), orderB.GetPrice());
                if (cmp == 0) { cmp = Long.compare(orderA.GetTime(), orderB.GetTime()); }
                if (cmp == 0) { cmp = Long.compare(orderA.GetID(), orderB.GetID()); }
                return cmp;
            });

    /**
     * Priority Queue for storing bid orders (buy orders).
     *
     * Orders are sorted by price in descending order. If two orders have the same price, they are sorted
     * by timestamp with the oldest order first.
     */
    private static final PriorityQueue<Order> _bidOrders
            = new PriorityQueue<Order>((orderA, orderB) -> {
                int cmp = -Long.compare(orderA.GetPrice(), orderB.GetPrice());
                if (cmp == 0) { cmp = Long.compare(orderA.GetTime(), orderB.GetTime()); }
                if (cmp == 0) { cmp = Long.compare(orderA.GetID(), orderB.GetID()); }
                return cmp;
            });

    /**
     * Calculates the spread between the best bid and the best ask.
     *
     * @return The spread (best bid - best ask).
     */
    public static long GetSpread()
    {
        long bidMax = 0;
        long askMin = 0;

        synchronized (_askOrders)
        {
            if (!_askOrders.isEmpty()) { askMin = _askOrders.peek().GetPrice(); }

            synchronized (_bidOrders)
            {
                if (!_bidOrders.isEmpty()) { bidMax = _bidOrders.peek().GetPrice(); }

                return bidMax - askMin;
            }
        }
    }

    static
    {
        //askRegistry.add()
    }

    /**
     * Processes a market order.
     *
     * @param order The market order to process.
     * @return The result of processing the order.
     */
    public static OrderResponse ProcessOrder(MarketOrder order)
    {
        Cart cart = order.CreateCart();

        if (order.GetType() == Type.ASK)
        {
            synchronized (_bidOrders)
            {
                for (Order bidOrder : _bidOrders)
                {
                    if (!cart.TrySell(bidOrder)) { break; }
                }

                if (cart.IsComplete())
                {
                    List<Order> emptyOrders = cart.SellAll();
                    for (Order bidOrder : emptyOrders) { _bidOrders.remove(bidOrder); }
                }
            }
        }

        if (order.GetType() == Type.BID)
        {
            synchronized (_askOrders)
            {
                for (Order askOrder : _askOrders)
                {
                    if (!cart.TryBuy(askOrder)) { break; }
                }

                List<Order> emptyOrders = cart.BuyAll();
                for (Order askOrder : emptyOrders) { _askOrders.remove(askOrder); }
            }
        }

        if (cart.IsComplete()) { return new OrderResponse(order.GetID()); }
        else { return new OrderResponse(-1); }
    }

    /**
     * Processes a limit order.
     *
     * @param order The limit order to process.
     * @return The result of processing the order.
     */
    public static OrderResponse ProcessOrder(LimitOrder order)
    {
        if (order.GetType() == Type.ASK)
        {
            LimitOrder askOrder = order;

            synchronized (_bidOrders)
            {
                while (!_bidOrders.isEmpty())
                {
                    Order bidOrder = _bidOrders.peek();
                    if (!askOrder.TrySellTo(bidOrder)) { break; }
                    if (bidOrder.GetSize() == 0) { _bidOrders.poll(); }
                    if (askOrder.GetSize() == 0) { break; }
                }

                if (askOrder.GetSize() > 0)
                {
                    synchronized (_askOrders) { _askOrders.add(askOrder); }
                }
            }
        }

        if (order.GetType() == Type.BID)
        {
            LimitOrder bidOrder = order;

            synchronized (_askOrders)
            {
                while (!_askOrders.isEmpty())
                {
                    Order askOrder = _askOrders.peek();
                    if (!bidOrder.TryBuyFrom(askOrder)) { break; }
                    if (askOrder.GetSize() == 0) { _askOrders.poll(); }
                    if (bidOrder.GetSize() == 0) { break; }
                }

                if (bidOrder.GetSize() > 0)
                {
                    synchronized (_bidOrders) { _bidOrders.add(bidOrder); }
                }
            }
        }

        return new OrderResponse(order.GetID());
    }

    /**
     * Processes a stop order.
     *
     * @param order The stop order to process.
     * @return The result of processing the order.
     */
    public static OrderResponse ProcessOrder(StopOrder order)
    {

        return null;
    }
}
