
package Networking;

/**
 * This enum defines the different types of operations that can be performed in the system.
 * These operations represent various actions such as user registration, login, order management, and retrieving price history.
 */
public enum OperationType
{
    // The operations available in the system.
    REGISTER,              // Represents user registration.
    UPDATE_CREDENTIALS,    // Represents updating user credentials (password change).
    LOGIN,                 // Represents user login.
    LOGOUT,                // Represents user logout.
    INSERT_MARKET_ORDER,   // Represents inserting a market order.
    INSERT_LIMIT_ORDER,    // Represents inserting a limit order.
    INSERT_STOP_ORDER,     // Represents inserting a stop order.
    CANCEL_ORDER,          // Represents canceling an order.
    GET_PRICE_HISTORY;     // Represents retrieving price history.

    /**
     * Converts the enum value to its corresponding string representation.
     * This method is useful for serializing the enum value into a string for transmission.
     *
     * @return A string representing the operation type.
     */
    public String ToString() {
        return switch (this) {
            case REGISTER -> "register";
            case UPDATE_CREDENTIALS -> "updateCredentials";
            case LOGIN -> "longin";
            case LOGOUT -> "logout";
            case INSERT_MARKET_ORDER -> "insertMarketOrder";
            case INSERT_LIMIT_ORDER -> "insertLimitOrder";
            case INSERT_STOP_ORDER -> "insertStopOrder";
            case CANCEL_ORDER -> "cancelOrder";
            case GET_PRICE_HISTORY -> "getPriceHistory";
        };
    }

    /**
     * Converts a string into its corresponding enum value.
     * This method is used to parse a string into an `OperationType` for handling operations based on user input or requests.
     *
     * @param text The string representing the operation type.
     * @return The corresponding `OperationType` enum value, or `null` if no match is found.
     */
    public static OperationType FromString(String text)
    {
        return switch (text) {
            case "register" -> REGISTER;
            case "updateCredentials" -> UPDATE_CREDENTIALS;
            case "longin" -> LOGIN;
            case "logout" -> LOGOUT;
            case "insertMarketOrder" -> INSERT_MARKET_ORDER;
            case "insertLimitOrder" -> INSERT_LIMIT_ORDER;
            case "insertStopOrder" -> INSERT_STOP_ORDER;
            case "cancelOrder" -> CANCEL_ORDER;
            case "getPriceHistory" -> GET_PRICE_HISTORY;
            default -> null;
        };
    }
}
