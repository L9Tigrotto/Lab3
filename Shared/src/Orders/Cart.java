
package Orders;

import Helpers.Tuple;

import java.util.ArrayList;
import java.util.List;

public class Cart
{
    private final List<Order> _orders;
    public final Order _order;
    public long _targetSize;
    public long _totalPrice;

    public Cart(Order order)
    {
        _orders = new ArrayList<>();
        _order = order;
        _targetSize = order.GetSize();
        _totalPrice = 0;
    }

    public boolean IsOrderConsumed() { return _targetSize == 0; }
    public long GetTotalPrice() { return _totalPrice; }

    public boolean CanSellTo(Order askOrder)
    {
        Tuple<Long, Long> size_price = _order.CanSellTo(askOrder);

        if (size_price.GetX() == 0) { return false; }

        _orders.add(askOrder);
        _targetSize -= size_price.GetX();
        _totalPrice += size_price.GetY();

        return _targetSize != 0;
    }

    public boolean CanBuyFrom(Order order)
    {
        Tuple<Long, Long> size_price = _order.CanBuyFrom(order);

        if (size_price.GetX() == 0) { return false; }

        _orders.add(order);
        _targetSize -= size_price.GetX();
        _totalPrice += size_price.GetY();

        return _targetSize != 0;
    }

    public List<Order> SellAll()
    {
        List<Order> consumedOrders = new ArrayList<>();

        for (Order order : _orders)
        {
            _order.TrySellTo(order);
            if (order.GetSize() == 0) { consumedOrders.add(order); }
        }

        return consumedOrders;
    }

    public List<Order> BuyAll()
    {
        List<Order> consumedOrders = new ArrayList<>();

        for (Order order : _orders)
        {
            _order.TryBuyFrom(order);
            if (order.GetSize() == 0) { consumedOrders.add(order); }
        }

        return consumedOrders;
    }
}
