package Orders;

import Messages.OrderResponse;

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
        _askOrders.add(new LimitOrder(0, Type.ASK, 100, 2500, System.currentTimeMillis(), null));
        _askOrders.add(new LimitOrder(1, Type.ASK, 200, 2500, System.currentTimeMillis(), null));
        _askOrders.add(new LimitOrder(2, Type.ASK, 600, 2200, System.currentTimeMillis(), null));
        _askOrders.add(new LimitOrder(3, Type.ASK, 100, 8000, System.currentTimeMillis(), null));
    }

    public static String Info(PriorityQueue<Order> orderQueue)
    {
        StringBuilder info = new StringBuilder();
        info.append(String.format("%10s%10s%10s\n", "Price", "Size", "Total"));

        if(orderQueue.isEmpty())
            return info.toString();

        PriorityQueue<Order> orders = new PriorityQueue<>(orderQueue);
        Order order = orders.poll();

        long price = order.GetPrice();
        long size = order.GetSize();

        while(!orders.isEmpty())
        {
            order = orders.poll();
            if(order.GetPrice() == price)
            {
                size += order.GetSize();
            }
            else
            {
                info.append(String.format("%10d%10d%10d\n", price, size, price * size));

                price = order.GetPrice();
                size = order.GetSize();
            }
        }

        info.append(String.format("%10d%10d%10d\n", price, size, price * size));
        return info.toString();
    }

    public static String GetStatus()
    {
        String status = "";
        status += String.format("%20s\n", "Ask Side");

        status += Info(_askOrders);

        status += "-------------------------------------\n";

        status += String.format("%20s\n", "Bid Side");
        status += Info(_bidOrders);

        return status;
    }

    /**
     * Processes a market order.
     *
     * @param order The market order to process.
     * @return The result of processing the order.
     */
    public static OrderResponse ProcessOrder(MarketOrder order)
    {
        // create a cart object to track the execution of the market order
        Cart cart = order.CreateCart();

        if (order.GetType() == Type.ASK)
        {
            // synchronize access to the bid order list to prevent race conditions
            synchronized (_bidOrders)
            {
                // iterate through existing bid orders and attempt to buy from them consuming the whole market order
                for (Order bidOrder : _bidOrders)
                {
                    if (!cart.TrySellTo(bidOrder)) { break; }
                }

                // if the market order is consumed, get the list of consumed bid orders that were bought and remove
                // them from the bid order list
                if (cart.IsOrderConsumed())
                {
                    List<Order> consumedOrders = cart.SellAll();
                    for (Order bidOrder : consumedOrders) { _bidOrders.remove(bidOrder); }
                }
            }
        }

        if (order.GetType() == Type.BID)
        {
            // synchronize access to the ask order list to prevent race conditions
            synchronized (_askOrders)
            {
                // iterate through existing ask orders and attempt to sell the whole market order to them
                for (Order askOrder : _askOrders)
                {
                    if (!cart.TryBuyFrom(askOrder)) { break; }
                }

                // if the market order is consumed, get the list of consumed ask orders that the market order was sold
                // to and remove them from the bid order list
                List<Order> emptyOrders = cart.BuyAll();
                for (Order askOrder : emptyOrders) { _askOrders.remove(askOrder); }
            }
        }

        // return success if the market order is consumed, otherwise return failure
        if (cart.IsOrderConsumed()) { return new OrderResponse(order.GetID()); }
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
            // synchronize access to the bid order list to prevent race conditions
            synchronized (_bidOrders)
            {
                // iterate through bid orders while there are some and the limit order isn't consumed
                while (!_bidOrders.isEmpty() && !order.IsConsumed())
                {
                    Order bidOrder = _bidOrders.peek();
                    if (bidOrder == null) { _bidOrders.poll(); continue; } // should not happen

                    if (!order.TrySellTo(bidOrder)) { break; }

                    // if the bid order is consumed, remove it from the list
                    if (bidOrder.IsConsumed()) { _bidOrders.poll(); }
                }

                // if the limit order isn't fully consumed, add it to the ask orders
                if (!order.IsConsumed())
                {
                    synchronized (_askOrders) { _askOrders.add(order); }
                }
            }
        }

        if (order.GetType() == Type.BID)
        {
            // synchronize access to the ask order list to prevent race conditions
            synchronized (_askOrders)
            {
                // iterate through ask orders while there are some and the limit order isn't consumed
                while (!_askOrders.isEmpty() && !order.IsConsumed())
                {
                    Order askOrder = _askOrders.peek();
                    if (askOrder == null) { _askOrders.poll(); continue; } // should not happen

                    if (!order.TryBuyFrom(askOrder)) { break; }

                    // if the ask order is consumed, remove it from the list
                    if (askOrder.IsConsumed()) { _askOrders.poll(); }
                }

                // if the limit order isn't fully consumed, add it to the bid orders
                if (!order.IsConsumed())
                {
                    synchronized (_bidOrders) { _bidOrders.add(order); }
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
