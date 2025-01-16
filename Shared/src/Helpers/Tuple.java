
package Helpers;

/**
 * A generic class representing a tuple with two elements of different types.
 * This class is used to store two related objects together in a single container.
 * It provides methods to retrieve both elements of the tuple.
 *
 * @param <T1> The type of the first element of the tuple.
 * @param <T2> The type of the second element of the tuple.
 */
public class Tuple<T1, T2>
{
    // the first element of the tuple
    private final T1 _x;

    // the second element of the tuple
    private final T2 _y;

    /**
     * Constructor to initialize the tuple with two elements.
     *
     * @param x The first element of the tuple.
     * @param y The second element of the tuple.
     */
    public Tuple(T1 x, T2 y)
    {
        this._x = x;
        this._y = y;
    }

    /**
     * Retrieves the first element of the tuple.
     *
     * @return the first element (of type T1) in the tuple.
     */
    public T1 GetX() { return _x; }

    /**
     * Retrieves the second element of the tuple.
     *
     * @return the second element (of type T2) in the tuple.
     */
    public T2 GetY() { return _y; }
}
