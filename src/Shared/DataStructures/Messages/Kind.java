package Shared.DataStructures;

public enum MessageKind
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
    MessageKind(int code) { _code = code; }

    public int GetCode() { return _code; }
}
