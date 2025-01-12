package Orders;

public enum Type
{
    MARKET(0),
    LIMIT(1),
    STOP(2);

    private final int _value;
    Type(int value) { _value = value; }

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