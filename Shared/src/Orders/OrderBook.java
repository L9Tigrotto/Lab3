package Orders;

import Helpers.Tuple;
import Messages.CancelOrderRequest;
import Messages.ClosedTradesNotification;
import Messages.OrderResponse;
import Messages.SimpleResponse;
import Users.User;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * The OrderBook class represents the entire order book, which manages and processes different types of orders
 * such as limit orders, market orders, and stop orders for both ask and bid sides.
 */
public class OrderBook
{
    // priority queues for managing ask and bid limit orders.

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
     * Gets the spread between the best bid and the best ask prices.
     * The spread is calculated as (best bid price - best ask price).
     *
     * @return The spread (difference between best bid and best ask).
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

    /**
     * Gets the best price for a given method (ASK or BID).
     *
     * @param method The order method (ASK or BID).
     * @return The best price available for the given method.
     */
    public static long GetBestPrice(Method method)
    {
        long price = 0;
        switch (method)
        {
            case ASK ->
            {
                synchronized (_askLimitOrders)
                {
                    if (!_askLimitOrders.isEmpty()) { price = _askLimitOrders.peek().GetPrice(); }
                }
            }
            case BID ->
            {
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
     * Processes a market order and attempts to match it with available limit orders in the order book.
     *
     * @param order The market order to process.
     * @return A tuple containing the order response and any notification messages.
     */
    public static Tuple<OrderResponse, String> ProcessOrder(MarketOrder order)
    {
        // create a cart object to track the execution of the market order
        Cart cart = order.CreateCart();
        String notificationMessage = "";

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
                        Tuple<List<Order>, ClosedTradesNotification> consumedOrders_notification = cart.SellAll();
                        List<Order> consumedOrders = consumedOrders_notification.GetX();
                        ClosedTradesNotification notification = consumedOrders_notification.GetY();
                        notification.Add(order, new Tuple<>(cart.GetConsumedSize(), cart.GetTotalPrice()));
                        notification.Terminate();
                        notificationMessage = notification.ToString();
                        notification.Close();

                        for (Order consumedOrder : consumedOrders) { _bidLimitOrders.remove(consumedOrder); }
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
                        Tuple<List<Order>, ClosedTradesNotification> consumedOrders_notification = cart.BuyAll();
                        List<Order> consumedOrders = consumedOrders_notification.GetX();
                        ClosedTradesNotification notification = consumedOrders_notification.GetY();
                        notification.Add(order, new Tuple<>(cart.GetConsumedSize(), cart.GetTotalPrice()));
                        notification.Terminate();
                        notificationMessage = notification.ToString();
                        notification.Close();

                        for (Order consumedOrder : consumedOrders) { _askLimitOrders.remove(consumedOrder); }
                    }
                }
            }
        }

        // return success if the market order is consumed, otherwise return failure
        if (cart.IsOrderConsumed()) { return new Tuple<>(new OrderResponse(order.GetID()), notificationMessage); }
        else { return new Tuple<>(OrderResponse.INVALID, notificationMessage); }
    }

    /**
     * Processes a limit order and attempts to match it with available limit and stop orders.
     *
     * @param order The limit order to process.
     * @return A tuple containing the order response and any notification messages.
     */
    public static Tuple<OrderResponse, List<String>> ProcessOrder(LimitOrder order)
    {
        // create a cart object to track the execution of the limit order
        Cart cart = order.CreateCart();
        List<String> notificationMessages = new ArrayList<>();

        switch (order.GetMethod())
        {
            case ASK ->
            {
                // synchronize access to the bid order list to prevent race conditions
                synchronized (_bidLimitOrders)
                {
                    for (Order bidOrder : _bidLimitOrders)
                    {
                        if (!cart.CanSellTo(bidOrder)) { break; }
                    }

                    Tuple<List<Order>, ClosedTradesNotification> consumedOrders_notification = cart.SellAll();
                    List<Order> consumedOrders = consumedOrders_notification.GetX();
                    ClosedTradesNotification notification = consumedOrders_notification.GetY();

                    if (cart.GetConsumedSize() > 0) { notification.Add(order, new Tuple<>(cart.GetConsumedSize(), cart.GetTotalPrice())); }
                    notification.Terminate();
                    if (!consumedOrders.isEmpty()) { notificationMessages.add(notification.ToString()); }
                    notification.Close();

                    for (Order consumedOrder : consumedOrders) { _bidLimitOrders.remove(consumedOrder); }

                    // if the limit order isn't fully consumed, add it to the ask orders
                    if (!order.IsConsumed())
                    {
                        synchronized (_askLimitOrders) { _askLimitOrders.add(order); }
                        synchronized (_bidStopOrders)
                        {
                            while (!_bidStopOrders.isEmpty())
                            {
                                StopOrder bidOrder = _bidStopOrders.peek();
                                String newMessage = TryProcessStopOrder(bidOrder);
                                if (!newMessage.isEmpty()) { notificationMessages.add(newMessage); }
                                if (order.IsConsumed()) { _bidStopOrders.remove(bidOrder); }
                                else { break; }
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
                    for (Order askOrder : _askLimitOrders)
                    {
                        if (!cart.CanBuyFrom(askOrder)) { break; }
                    }

                    Tuple<List<Order>, ClosedTradesNotification> consumedOrders_notification = cart.BuyAll();
                    List<Order> consumedOrders = consumedOrders_notification.GetX();
                    ClosedTradesNotification notification = consumedOrders_notification.GetY();
                    notification.Add(order, new Tuple<>(cart.GetConsumedSize(), cart.GetTotalPrice()));
                    notification.Terminate();
                    if (cart.GetConsumedSize() > 0) { notificationMessages.add(notification.ToString()); }
                    notification.Close();

                    for (Order consumedOrder : consumedOrders) { _askLimitOrders.remove(consumedOrder); }

                    // if the limit order isn't fully consumed, add it to the bid orders
                    if (!order.IsConsumed())
                    {
                        synchronized (_bidLimitOrders) { _bidLimitOrders.add(order); }
                        synchronized (_askStopOrders)
                        {
                            while (!_askStopOrders.isEmpty())
                            {
                                StopOrder askOrder = _askStopOrders.peek();
                                String newMessage = TryProcessStopOrder(askOrder);
                                if (!newMessage.isEmpty()) { notificationMessages.add(newMessage); }
                                if (askOrder.IsConsumed()) { _askStopOrders.remove(askOrder); }
                                else { break; }
                            }
                        }
                    }
                }
            }
        }

        return new Tuple<>(new OrderResponse(order.GetID()), notificationMessages);
    }

    /**
     * Processes a stop order and attempts to match it with available limit orders.
     * If a stop order is triggered, it behaves like a market order.
     *
     * @param order The stop order to process.
     * @return A tuple containing the order response and any notification messages.
     */
    public static Tuple<OrderResponse, String> ProcessOrder(StopOrder order)
    {
        String notificationMessage = TryProcessStopOrder(order);

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

        return new Tuple<>(new OrderResponse(order.GetID()), notificationMessage);
    }

    /**
     * Attempts to trigger and process a stop order by matching it with the available orders.
     * If a stop order's conditions are met, it gets executed as a market order.
     *
     * @param order The stop order to process.
     * @return Any notification message generated during the execution of the stop order.
     */
    private static String TryProcessStopOrder(StopOrder order)
    {
        // create a cart object to track the execution of the stop order
        Cart cart = order.CreateCart();
        String notificationMessage = "";

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
                        Tuple<List<Order>, ClosedTradesNotification> consumedOrders_notification = cart.SellAll();
                        List<Order> consumedOrders = consumedOrders_notification.GetX();
                        ClosedTradesNotification notification = consumedOrders_notification.GetY();
                        notification.Add(order, new Tuple<>(cart.GetConsumedSize(), cart.GetTotalPrice()));
                        notification.Terminate();
                        notificationMessage = notification.ToString();
                        notification.Close();

                        for (Order consumedOrder : consumedOrders) { _bidLimitOrders.remove(consumedOrder); }
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
                        Tuple<List<Order>, ClosedTradesNotification> consumedOrders_notification = cart.BuyAll();
                        List<Order> consumedOrders = consumedOrders_notification.GetX();
                        ClosedTradesNotification notification = consumedOrders_notification.GetY();
                        notification.Add(order, new Tuple<>(cart.GetConsumedSize(), cart.GetTotalPrice()));
                        notification.Terminate();
                        notificationMessage = notification.ToString();
                        notification.Close();

                        for (Order consumedOrder : consumedOrders) { _askLimitOrders.remove(consumedOrder); }
                    }
                }
            }
        }

        return notificationMessage;
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
