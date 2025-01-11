package Networking;

public enum OperationType
{
    REGISTER(0),
    UPDATE_CREDENTIALS(1),
    LOGIN(2),
    LOGOUT(3),
    INSERT_MARKET_ORDER(4),
    INSERT_LIMIT_ORDER(5),
    INSERT_STOP_ORDER(6),
    CANCEL_ORDER(7),
    GET_PRICE_HISTORY(8);

    private final int _value;
    OperationType(int value) {
        _value = value;
    }

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
