
package Orders;

import Helpers.Tuple;
import Messages.ClosedTradesNotification;

import java.util.ArrayList;
import java.util.List;

public class Cart
{
    private final List<Order> _orders;
    private final Order _order;
    private long _targetSize;
    private long _remainingSize;
    private long _totalPrice;

    public Cart(Order order)
    {
        _orders = new ArrayList<>();
        _order = order;
        _targetSize = order.GetSize();
        _remainingSize = order.GetSize();
        _totalPrice = 0;
    }

    public long GetConsumedSize() { return _targetSize - _remainingSize; }
    public boolean IsOrderConsumed() { return _remainingSize == 0; }
    public long GetTotalPrice() { return _totalPrice; }

    public boolean CanSellTo(Order askOrder)
    {
        Tuple<Long, Long> size_price = _order.CanSellTo(askOrder);

        if (size_price.GetX() == 0) { return false; }

        _orders.add(askOrder);
        _remainingSize -= size_price.GetX();
        _totalPrice += size_price.GetY();

        return _remainingSize != 0;
    }

    public boolean CanBuyFrom(Order order)
    {
        Tuple<Long, Long> size_price = _order.CanBuyFrom(order);

        if (size_price.GetX() == 0) { return false; }

        _orders.add(order);
        _remainingSize -= size_price.GetX();
        _totalPrice += size_price.GetY();

        return _remainingSize != 0;
    }

    public Tuple<List<Order>, ClosedTradesNotification> SellAll()
    {
        List<Order> consumedOrders = new ArrayList<>();
        ClosedTradesNotification notification = new ClosedTradesNotification();

        for (Order order : _orders)
        {
            _order.TrySellTo(order, notification);
            if (order.GetSize() == 0) { consumedOrders.add(order); }
        }

        return new Tuple<>(consumedOrders, notification);
    }

    public Tuple<List<Order>, ClosedTradesNotification> BuyAll()
    {
        List<Order> consumedOrders = new ArrayList<>();
        ClosedTradesNotification notification = new ClosedTradesNotification();

        for (Order order : _orders)
        {
            _order.TryBuyFrom(order, notification);
            if (order.GetSize() == 0) { consumedOrders.add(order); }
        }

        return new Tuple<>(consumedOrders, notification);
    }
}
