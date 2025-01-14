package Orders;

import Messages.CancelOrderRequest;
import Messages.OrderResponse;
import Messages.SimpleResponse;
import Users.User;

import java.util.List;
import java.util.PriorityQueue;

public class OrderBook
{
    @SuppressWarnings("ComparatorCombinators")
    private static final PriorityQueue<LimitOrder> _askLimitOrders
            = new PriorityQueue<>((orderA, orderB) -> {
                int cmp = Long.compare(orderA.GetPrice(), orderB.GetPrice());
                if (cmp == 0) { cmp = Long.compare(orderA.GetTimestamp(), orderB.GetTimestamp()); }
                if (cmp == 0) { cmp = Long.compare(orderA.GetID(), orderB.GetID()); }
                return cmp;
            });

    private static final PriorityQueue<LimitOrder> _bidLimitOrders
            = new PriorityQueue<>((orderA, orderB) -> {
                int cmp = -Long.compare(orderA.GetPrice(), orderB.GetPrice());
                if (cmp == 0) { cmp = Long.compare(orderA.GetTimestamp(), orderB.GetTimestamp()); }
                if (cmp == 0) { cmp = Long.compare(orderA.GetID(), orderB.GetID()); }
                return cmp;
            });

    @SuppressWarnings("ComparatorCombinators")
    private static final PriorityQueue<StopOrder> _askStopOrders
            = new PriorityQueue<>((orderA, orderB) -> {
                int cmp = Long.compare(orderA.GetPrice(), orderB.GetPrice());
                if (cmp == 0) { cmp = Long.compare(orderA.GetTimestamp(), orderB.GetTimestamp()); }
                if (cmp == 0) { cmp = Long.compare(orderA.GetID(), orderB.GetID()); }
                return cmp;
            });

    private static final PriorityQueue<StopOrder> _bidStopOrders
            = new PriorityQueue<>((orderA, orderB) -> {
                int cmp = -Long.compare(orderA.GetStopPrice(), orderB.GetStopPrice());
                if (cmp == 0) { cmp = Long.compare(orderA.GetTimestamp(), orderB.GetTimestamp()); }
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

        synchronized (_askLimitOrders)
        {
            if (!_askLimitOrders.isEmpty()) { askMin = _askLimitOrders.peek().GetPrice(); }

            synchronized (_bidLimitOrders)
            {
                if (!_bidLimitOrders.isEmpty()) { bidMax = _bidLimitOrders.peek().GetPrice(); }

                return bidMax - askMin;
            }
        }
    }

    public static long GetBestPrice(Method method)
    {
        long price = 0;
        switch (method)
        {
            case ASK -> {
                synchronized (_askLimitOrders)
                {
                    if (!_askLimitOrders.isEmpty()) { price = _askLimitOrders.peek().GetPrice(); }
                }
            }
            case BID -> {
                synchronized (_bidLimitOrders)
                {
                    if (!_bidLimitOrders.isEmpty()) { price = _bidLimitOrders.peek().GetPrice(); }
                }
            }
        }

        return price;
    }

    private static void AppendLimitInfo(StringBuilder status, PriorityQueue<LimitOrder> orderQueue)
    {
        status.append(String.format("%10s%10s%10s\n", "Price", "Size", "Total"));

        for (Order order : orderQueue)
        {
            status.append(String.format("%10d%10d%10d\n", order.GetPrice(), order.GetSize(), order.GetPrice() * order.GetSize()));
        }
    }

    private static void AppendStopInfo(StringBuilder status, PriorityQueue<StopOrder> orderQueue)
    {
        status.append(String.format("%10s%10s%10s\n", "Price", "Size", "Total"));

        for (Order order : orderQueue)
        {
            status.append(String.format("%10s%10d%10s\n", "-", order.GetSize(), "-"));
        }
    }

    public static String PrintStatus()
    {
        StringBuilder status = new StringBuilder();

        status.append("LIMIT ORDERS\n");
        status.append(String.format("%20s\n", "Ask Side"));
        synchronized (_askLimitOrders) { AppendLimitInfo(status, _askLimitOrders); }

        status.append("-------------------------------------\n");

        status.append(String.format("%20s\n", "Bid Sid"));
        synchronized (_bidLimitOrders) { AppendLimitInfo(status, _bidLimitOrders); }

        status.append("\n\n\nSTOP ORDERS\n");
        status.append(String.format("%20s\n", "Ask Side"));
        synchronized (_askStopOrders) { AppendStopInfo(status, _askStopOrders); }

        status.append("-------------------------------------\n");

        status.append(String.format("%20s\n", "Bid Sid"));
        synchronized (_bidStopOrders) { AppendStopInfo(status, _bidStopOrders); }

        return status.toString();
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

        switch (order.GetMethod())
        {
            case ASK ->
            {
                // synchronize access to the bid order list to prevent race conditions
                synchronized (_bidLimitOrders)
                {
                    // iterate through existing bid orders and attempt to buy from them consuming the whole market order
                    for (Order bidOrder : _bidLimitOrders)
                    {
                        if (!cart.CanSellTo(bidOrder)) { break; }
                    }

                    // if the market order is consumed, get the list of consumed bid orders that were bought and remove
                    // them from the bid order list
                    if (cart.IsOrderConsumed())
                    {
                        List<Order> consumedOrders = cart.SellAll();
                        for (Order bidOrder : consumedOrders) { _bidLimitOrders.remove(bidOrder); }
                    }
                }
            }
            case BID ->
            {
                // synchronize access to the ask order list to prevent race conditions
                synchronized (_askLimitOrders)
                {
                    // iterate through existing ask orders and attempt to sell the whole market order to them
                    for (Order askOrder : _askLimitOrders)
                    {
                        if (!cart.CanBuyFrom(askOrder)) { break; }
                    }

                    // if the market order is consumed, get the list of consumed ask orders that the market order was sold
                    // to and remove them from the bid order list
                    if (cart.IsOrderConsumed())
                    {
                        List<Order> emptyOrders = cart.BuyAll();
                        for (Order askOrder : emptyOrders) { _askLimitOrders.remove(askOrder); }
                    }
                }
            }
        }

        // return success if the market order is consumed, otherwise return failure
        if (cart.IsOrderConsumed()) { return new OrderResponse(order.GetID()); }
        else { return OrderResponse.INVALID; }
    }

    /**
     * Processes a limit order.
     *
     * @param order The limit order to process.
     * @return The result of processing the order.
     */
    public static OrderResponse ProcessOrder(LimitOrder order)
    {
        switch (order.GetMethod())
        {
            case ASK ->
            {
                // synchronize access to the bid order list to prevent race conditions
                synchronized (_bidLimitOrders)
                {
                    // iterate through bid orders while there are some and the limit order isn't consumed
                    while (!_bidLimitOrders.isEmpty() && !order.IsConsumed())
                    {
                        Order bidOrder = _bidLimitOrders.peek();
                        if (bidOrder == null) { _bidLimitOrders.poll(); continue; } // should not happen

                        if (!order.TrySellTo(bidOrder)) { break; }

                        // if the bid order is consumed, remove it from the list
                        if (bidOrder.IsConsumed()) { _bidLimitOrders.poll(); }
                    }

                    // if the limit order isn't fully consumed, add it to the ask orders
                    if (!order.IsConsumed())
                    {
                        synchronized (_askLimitOrders) { _askLimitOrders.add(order); }
                        synchronized (_bidStopOrders)
                        {
                            if (!_bidStopOrders.isEmpty())
                            {
                                StopOrder bidOrder = _bidStopOrders.peek();
                                TryProcessStopOrder(bidOrder);
                                if (bidOrder.IsConsumed()) { _bidStopOrders.remove(bidOrder); }
                            }
                        }
                    }
                }
            }
            case BID ->
            {
                // synchronize access to the ask order list to prevent race conditions
                synchronized (_askLimitOrders)
                {
                    // iterate through ask orders while there are some and the limit order isn't consumed
                    while (!_askLimitOrders.isEmpty() && !order.IsConsumed())
                    {
                        Order askOrder = _askLimitOrders.peek();
                        if (askOrder == null) { _askLimitOrders.poll(); continue; } // should not happen

                        if (!order.TryBuyFrom(askOrder)) { break; }

                        // if the ask order is consumed, remove it from the list
                        if (askOrder.IsConsumed()) { _askLimitOrders.poll(); }
                    }

                    // if the limit order isn't fully consumed, add it to the bid orders
                    if (!order.IsConsumed())
                    {
                        synchronized (_bidLimitOrders) { _bidLimitOrders.add(order); }
                        synchronized (_askStopOrders)
                        {
                            if (!_askStopOrders.isEmpty())
                            {
                                StopOrder askOrder = _askStopOrders.peek();
                                TryProcessStopOrder(askOrder);
                                if (askOrder.IsConsumed()) { _askStopOrders.remove(askOrder); }
                            }
                        }
                    }
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
        TryProcessStopOrder(order);

        if (!order.IsConsumed())
        {
            switch (order.GetMethod())
            {
                case ASK ->
                {
                    synchronized (_askStopOrders) { _askStopOrders.add(order); }
                }
                case BID ->
                {
                    synchronized (_bidStopOrders) { _bidStopOrders.add(order); }
                }
            }
        }

        return new OrderResponse(order.GetID());
    }

    private static void TryProcessStopOrder(StopOrder order)
    {
        // create a cart object to track the execution of the stop order
        Cart cart = order.CreateCart();

        switch (order.GetMethod())
        {
            case ASK ->
            {
                // synchronize access to the bid order list to prevent race conditions
                synchronized (_bidLimitOrders)
                {
                    // iterate through existing bid orders and attempt to buy from them consuming the whole market order
                    for (Order bidOrder : _bidLimitOrders)
                    {
                        if (!cart.CanSellTo(bidOrder)) { break; }
                    }

                    // if the stop order is consumed, get the list of consumed bid orders that were bought and remove
                    // them from the bid order list
                    if (cart.IsOrderConsumed() && cart.GetTotalPrice() <= order.GetStopPrice())
                    {
                        List<Order> consumedOrders = cart.SellAll();
                        for (Order bidOrder : consumedOrders) { _bidLimitOrders.remove(bidOrder); }
                    }
                }
            }
            case BID ->
            {
                // synchronize access to the ask order list to prevent race conditions
                synchronized (_askLimitOrders)
                {
                    // iterate through existing ask orders and attempt to sell the whole market order to them
                    for (Order askOrder : _askLimitOrders)
                    {
                        if (!cart.CanBuyFrom(askOrder)) { break; }
                    }

                    // if the stop order is consumed, get the list of consumed ask orders that the market order was sold
                    // to and remove them from the bid order list
                    if (cart.IsOrderConsumed() && cart.GetTotalPrice() >= order.GetStopPrice())
                    {
                        List<Order> emptyOrders = cart.BuyAll();
                        for (Order askOrder : emptyOrders) { _askLimitOrders.remove(askOrder); }
                    }
                }
            }
        }
    }

    public static SimpleResponse TryCancelOrder(CancelOrderRequest request, User user)
    {
        synchronized (_askLimitOrders)
        {
            for (Order askOrder : _askLimitOrders)
            {
                if (askOrder.GetID() != request.GetOrderID()) { continue; }
                if (!askOrder.GetUser().GetUsername().equals(user.GetUsername())) { return CancelOrderRequest.ORDER_BELONG_TO_DIFFERENT_USER; }

                _askLimitOrders.remove(askOrder);
                return CancelOrderRequest.OK;
            }
        }

        synchronized (_bidLimitOrders)
        {
            for (Order bidOrder : _bidLimitOrders)
            {
                if (bidOrder.GetID() != request.GetOrderID()) { continue; }
                if (!bidOrder.GetUser().GetUsername().equals(user.GetUsername())) { return CancelOrderRequest.ORDER_BELONG_TO_DIFFERENT_USER; }

                _bidLimitOrders.remove(bidOrder);
                return CancelOrderRequest.OK;
            }
        }

        synchronized (_askStopOrders)
        {
            for (Order askOrder : _askStopOrders)
            {
                if (askOrder.GetID() != request.GetOrderID()) { continue; }
                if (!askOrder.GetUser().GetUsername().equals(user.GetUsername())) { return CancelOrderRequest.ORDER_BELONG_TO_DIFFERENT_USER; }

                _askStopOrders.remove(askOrder);
                return CancelOrderRequest.OK;
            }
        }

        synchronized (_bidStopOrders)
        {
            for (Order bidOrder : _bidStopOrders)
            {
                if (bidOrder.GetID() != request.GetOrderID()) { continue; }
                if (!bidOrder.GetUser().GetUsername().equals(user.GetUsername())) { return CancelOrderRequest.ORDER_BELONG_TO_DIFFERENT_USER; }

                _bidStopOrders.remove(bidOrder);
                return CancelOrderRequest.OK;
            }
        }

        return CancelOrderRequest.ORDER_DOES_NOT_EXISTS;
    }
}
