package Orders;

public enum Method
{
    BID,
    ASK;

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
