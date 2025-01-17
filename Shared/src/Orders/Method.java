
package Orders;

/**
 * Enum representing the different methods for placing orders: BID and ASK.
 * Each method represents an action in the context of a trading system.
 */
public enum Method
{
    BID,    // A bid order, represents a buy action.
    ASK;    // An ask order, represents a sell action.

    /**
     * Converts the enum value to its string representation.
     *
     * @return A string representation of the order method (e.g., "bid", "ask").
     */
    public String ToString()
    {
        return switch (this)
        {
            case BID -> "bid";
            case ASK -> "ask";
        };
    }

    /**
     * Converts a string to its corresponding Method enum value.
     *
     * @param text The string to be converted (e.g., "bid", "ask").
     * @return The corresponding Method enum value, or null if the input string doesn't match any known method.
     */
    public static Method FromString(String text)
    {
        return switch (text) {
            case "bid" -> BID;
            case "ask" -> ASK;
            default -> null;
        };
    }
}
