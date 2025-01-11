
package Orders;

import java.util.ArrayList;
import java.util.List;

public class Cart
{
    private final List<Order> _orders;
    public final MarketOrder _order;
    public long _targetSize;

    public Cart(MarketOrder order)
    {
        _orders = new ArrayList<Order>();
        _order = order;
        _targetSize = order.GetSize();
    }

    public boolean TrySellTo(Order askOrder)
    {
        long size = _order.CanSellTo(askOrder);

        if (size == 0) { return false; }

        _orders.add(askOrder);
        _targetSize -= size;

        return _targetSize != 0;
    }

    public boolean TryBuyFrom(Order order)
    {
        long size = _order.CanBuyFrom(order);

        if (size == 0) { return false; }

        _orders.add(order);
        _targetSize -= size;

        return _targetSize != 0;
    }

    public boolean IsOrderConsumed() { return _targetSize == 0; }

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
