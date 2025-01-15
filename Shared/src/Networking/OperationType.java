package Networking;

public enum OperationType
{
    REGISTER,
    UPDATE_CREDENTIALS,
    LOGIN,
    LOGOUT,
    INSERT_MARKET_ORDER,
    INSERT_LIMIT_ORDER,
    INSERT_STOP_ORDER,
    CANCEL_ORDER,
    GET_PRICE_HISTORY;

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
