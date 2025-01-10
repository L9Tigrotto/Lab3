
package Orders;

import java.util.ArrayList;
import java.util.List;

public class Cart
{
    private final List<Order> _orders;
    public long _size;

    public Cart(long size)
    {
        _orders = new ArrayList<Order>();
        _size = size;
    }

    public void Add(Order order, long size)
    {
        _orders.add(order);
        _size -= size;
    }

    public boolean IsComplete() { return _size == 0; }

    public void BuyAll()
    {

    }
}
