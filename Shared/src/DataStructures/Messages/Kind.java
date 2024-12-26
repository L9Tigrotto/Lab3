package DataStructures.Messages;

public enum Kind
{
    Register(1),
    UpdateCredentials(2),
    Login(3),
    Logout(4),
    InsertLimitOrder(5),
    InsertMarketOrder(6),
    InsertStopOrder(7),
    CancelOrder(8),
    GetPriceHistory(9);

    private final int _code;
    Kind(int code) { _code = code; }

    public int GetCode() { return _code; }

    public static Kind FromInt(int code)
    {
        return switch (code)
        {
            case 1 -> Register;
            case 2 -> UpdateCredentials;
            case 3 -> Login;
            case 4 -> Logout;
            case 5 -> InsertLimitOrder;
            case 6 -> InsertMarketOrder;
            case 7 -> InsertStopOrder;
            case 8 -> CancelOrder;
            case 9 -> GetPriceHistory;
            default -> throw new IllegalArgumentException("Invalid code");
        };
    }
}
