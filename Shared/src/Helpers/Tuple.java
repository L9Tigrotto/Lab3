
package Helpers;

public class Tuple<T1, T2>
{
    private T1 _x;
    private T2 _y;

    public Tuple(T1 x, T2 y)
    {
        this._x = x;
        this._y = y;
    }

    public T1 GetX() { return _x; }
    public T2 GetY() { return _y; }
}
