
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

    public boolean TrySell(Order order)
    {
        long size = _order.CanSellTo(order);

        if (size == 0) { return false; }

        _orders.add(order);
        _targetSize -= size;

        return _targetSize != 0;
    }

    public boolean TryBuy(Order order)
    {
        long size = _order.CanBuyFrom(order);

        if (size == 0) { return false; }

        _orders.add(order);
        _targetSize -= size;

        return _targetSize != 0;
    }

    public boolean IsComplete() { return _targetSize == 0; }

    public List<Order> SellAll()
    {
        List<Order> emptyOrders = new ArrayList<>();

        for (Order order : _orders)
        {
            _order.TrySellTo(order);
            if (order.GetSize() == 0) { emptyOrders.add(order); }
        }

        return emptyOrders;
    }

    public List<Order> BuyAll()
    {
        List<Order> emptyOrders = new ArrayList<>();

        for (Order order : _orders)
        {
            _order.TryBuyFrom(order);
            if (order.GetSize() == 0) { emptyOrders.add(order); }
        }

        return emptyOrders;
    }
}
