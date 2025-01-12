package Orders;

public enum Method
{
    BID(0),
    ASK(1);

    private final int _value;
    Method(int value) { _value = value; }

    public String ToString()
    {
        return switch (this)
        {
            case BID -> "bid";
            case ASK -> "ask";
        };
    }

    public static Method FromString(String text)
    {
        return switch (text) {
            case "bid" -> BID;
            case "ask" -> ASK;
            default -> null;
        };
    }
}
