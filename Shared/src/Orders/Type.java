package Orders;

public enum Type
{
    MARKET,
    LIMIT,
    STOP;

    public String ToString()
    {
        return switch (this)
        {
            case MARKET -> "market";
            case LIMIT -> "limit";
            case STOP -> "stop";
        };
    }

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