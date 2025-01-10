package Orders;

public enum Type
{
    BID(0),
    ASK(1);

    private final int _value;
    Type (int value) { _value = value; }

    public String ToString()
    {
        return switch (this)
        {
            case BID -> "bid";
            case ASK -> "ask";
            default -> "unknown";
        };
    }
}
