package Orders;

/**
 * Enum representing the different types of orders: MARKET, LIMIT, and STOP.
 * Each type corresponds to a different kind of order in a trading system.
 */
public enum Type
{
    MARKET,  // A market order, where the trade is executed immediately at the current market price.
    LIMIT,   // A limit order, where the trade is executed at a specified price or better.
    STOP;    // A stop order, where the trade is executed once a certain stop price is reached.

    /**
     * Converts the enum value to its string representation.
     *
     * @return A string representation of the order type (e.g., "market", "limit", "stop").
     */
    public String ToString()
    {
        return switch (this)
        {
            case MARKET -> "market";
            case LIMIT -> "limit";
            case STOP -> "stop";
        };
    }

    /**
     * Converts a string to its corresponding Type enum value.
     *
     * @param text The string to be converted (e.g., "market", "limit", "stop").
     * @return The corresponding Type enum value, or null if the input string doesn't match any known type.
     */
    public static Type FromString(String text)
    {
        return switch (text) {
            case "market" -> MARKET;
            case "limit" -> LIMIT;
            case "stop" -> STOP;
            default -> null;
        };
    }
}