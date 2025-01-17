
package Orders;

import Helpers.Tuple;

/**
 * Represents a historical record of an order trade, capturing the details of the trade.
 * This class is used to store information about completed trades, including order IDs,
 * methods, types, sizes, prices, and timestamps.
 */
public class HistoryRecord
{
    // unique identifier of the order
    private final long _orderID;

    // the method used for the order (Bid or Ask)
    private final Method _method;

    // the type of the order (Market, Limit, Stop)
    private final Type _type;

    // the size of the trade (number of items traded)
    private final long _size;

    // The price at which the trade was executed
    private final long _price;

    // the timestamp when the trade was recorded
    private final long _timestamp;

    /**
     * Constructor for creating a history record with complete details.
     *
     * @param orderID The unique ID of the order.
     * @param method The method of the order (Bid or Ask).
     * @param type The type of the order (Market, Limit, Stop).
     * @param size The size (quantity) of the trade.
     * @param price The price at which the trade was executed.
     * @param timestamp The timestamp when the trade occurred.
     */
    public HistoryRecord(long orderID, Method method, Type type, long size, long price, long timestamp)
    {
        _orderID = orderID;
        _method = method;
        _type = type;
        _size = size;
        _price = price;
        _timestamp = timestamp;
    }

    /**
     * Constructor for creating a history record from an order and a size-price tuple.
     * This constructor simplifies creating a history record based on an order that has
     * been partially or fully executed, using the size-price information of the trade.
     *
     * @param order The order involved in the trade.
     * @param size_price A Tuple containing the size and price of the trade.
     */
    public HistoryRecord(Order order, Tuple<Long, Long> size_price)
    {
        _orderID = order.GetID();
        _method = order.GetMethod();
        _type = order.GetType();
        _size = size_price.GetX();
        _price = size_price.GetY();
        _timestamp = System.currentTimeMillis();
    }

    // getter methods to access the record's details

    public long GetID() { return _orderID; }
    public Method GetMethod() { return _method; }
    public Type GetType() { return _type; }
    public long GetSize() { return _size; }
    public long GetPrice() { return _price; }
    public long GetTimestamp() { return _timestamp; }
}
