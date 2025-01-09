package Orders;

import DataStructures.LimitOrder;
import DataStructures.MarketOrder;
import DataStructures.StopOrder;
import Messages.OrderResponse;

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
                if (cmp == 0) { cmp = Long.compare(orderA.GetTimestamp(), orderB.GetTimestamp()); }
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
                if (cmp == 0) { cmp = Long.compare(orderA.GetTimestamp(), orderB.GetTimestamp()); }
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

        return null;
    }

    /**
     * Processes a limit order.
     *
     * @param order The limit order to process.
     * @return The result of processing the order.
     */
    public static OrderResponse ProcessOrder(LimitOrder order)
    {

        return null;
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
