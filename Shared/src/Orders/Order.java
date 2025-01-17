
package Orders;

import Helpers.Tuple;
import Messages.ClosedTradesNotification;
import Users.User;

/**
 * Represents an order in the trading system.
 * Orders can be of different types (e.g., market, limit, stop) and methods (bid/ask).
 */
public class Order
{
    // unique identifier for the order
    private final long _id;

    // type of the order (Market, Limit, Stop)
    public final Type _type;

    // method (Bid or Ask)
    private final Method _method;

    // the quantity of the order
    private long _size;

    // the price (change usage based on the specified class)
    private final long _price;

    // the timestamp when the order was created
    private final long _timestamp;

    // the user who placed the order (null for client deserialization)
    private final User _user;

    /**
     * Constructor for creating an order with a user.
     *
     * @param id The order ID
     * @param type The type of the order (Market, Limit, Stop)
     * @param method The method (Bid or Ask)
     * @param size The size (quantity) of the order
     * @param price The price of the order
     * @param user The user who placed the order
     */
    public Order(long id, Type type, Method method, long size, long price, User user)
    {
        _id = id;
        _type = type;
        _method = method;
        _size = size;
        _price = price;
        _timestamp = System.currentTimeMillis();
        _user = user;
    }

    /**
     * Constructor for creating an order without a user (e.g., system orders).
     *
     * @param id The order ID
     * @param type The type of the order (Market, Limit, Stop)
     * @param method The method (Bid or Ask)
     * @param size The size (quantity) of the order
     * @param price The price of the order
     * @param timestamp The timestamp when the order was created
     */
    public Order(long id, Type type, Method method, long size, long price, long timestamp)
    {
        _id = id;
        _type = type;
        _method = method;
        _size = size;
        _price = price;
        _timestamp = System.currentTimeMillis();
        _user = null;
    }

    // getter methods

    public long GetID() { return _id; }
    public Type GetType() { return _type; }
    public Method GetMethod() { return _method; }
    public long GetSize() { return _size; }
    public long GetPrice() { return _price; }
    public long GetTimestamp() { return _timestamp; }
    public User GetUser() { return _user; }

    /**
     * Checks if the order is fully consumed (size is zero).
     *
     * @return True if the size is zero (order is consumed), otherwise false.
     */
    public boolean IsConsumed() { return _size == 0; }

    /**
     * Determines if the order is a "sell" order.
     *
     * @return True if the order is an Ask (Sell), otherwise false.
     */
    public boolean WantToSell() { return _method == Method.ASK; }

    /**
     * Determines if the order is a "buy" order.
     *
     * @return True if the order is a Bid (Buy), otherwise false.
     */
    public boolean WantToBuy() { return _method == Method.BID; }

    public boolean WantToSellAt(long price) { return WantToSell(); }
    public boolean WantToBuyAt(long price) { return WantToBuy(); }

    /**
     * Decreases the size of the order based on the quantity of the transaction.
     *
     * @param size_price A Tuple containing the size and price of the trade.
     */
    protected void DecreaseSize(Tuple<Long, Long> size_price) { _size -= size_price.GetX(); }

    /**
     * Creates a Cart object representing the order.
     *
     * @return A new Cart object representing this order.
     */
    public Cart CreateCart() { return new Cart(this); }

    /**
     * Checks if the order can sell to another order based on price and size.
     *
     * @param order The order being bought from.
     * @return A Tuple containing the size and price of the trade (0, 0 if no match).
     */
    public Tuple<Long, Long> CanSellTo(Order order)
    {
        long price = Math.min(_price, order.GetPrice());
        long size = Math.min(_size, order.GetSize());

        if (!WantToSellAt(price) || !order.WantToBuyAt(price)) { return new Tuple<>(0L, 0L); }

        return new Tuple<>(size, price);
    }

    /**
     * Checks if the order can buy from another order based on price and size.
     *
     * @param order The order being sold to.
     * @return A Tuple containing the size and price of the trade (0, 0 if no match).
     */
    public Tuple<Long, Long> CanBuyFrom(Order order)
    {
        long price = Math.min(_price, order.GetPrice());
        long size = Math.min(_size, order.GetSize());

        if (!WantToBuyAt(price) || !order.WantToSellAt(price)) { return new Tuple<>(0L, 0L); }

        return new Tuple<>(size, price);
    }

    /**
     * Attempts to sell to another order, updating the order sizes and creating a history record.
     *
     * @param order The order being sold to.
     * @param notification The notification object for closed trades.
     * @return True if the sale was successful, otherwise false.
     */
    public boolean TrySellTo(Order order, ClosedTradesNotification notification)
    {
        Tuple<Long, Long> size_price = CanSellTo(order);
        if (size_price.GetX() == 0) { return false; }

        DecreaseSize(size_price);
        order.DecreaseSize(size_price);

        if (order.IsConsumed()) { notification.Add(order, size_price); }

        HistoryRecord record = new HistoryRecord(this, size_price);
        HistoryRecordCollection.Add(record);

        return true;
    }

    /**
     * Attempts to buy from another order, updating the order sizes and creating a history record.
     *
     * @param order The order being bought from.
     * @param notification The notification object for closed trades.
     * @return True if the purchase was successful, otherwise false.
     */
    public boolean TryBuyFrom(Order order, ClosedTradesNotification notification)
    {
        Tuple<Long, Long> size_price = CanBuyFrom(order);
        if (size_price.GetX() == 0) { return false; }

        DecreaseSize(size_price);
        order.DecreaseSize(size_price);

        if (order.IsConsumed()) { notification.Add(order, size_price); }

        HistoryRecord record = new HistoryRecord(this, size_price);
        HistoryRecordCollection.Add(record);

        return true;
    }
}
