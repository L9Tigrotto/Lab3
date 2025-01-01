
package Messages;

public enum MessageKind
{
    // Request kind
    NullRequest(true, 0),
    RegisterRequest(true, 1),
    UpdateCredentialsRequest(true, 2),
    LoginRequest(true, 3),
    LogoutRequest(true, 4),
    InsertLimitOrderRequest(true, 5),
    InsertMarketOrderRequest(true, 6),
    InsertStopOrderRequest(true, 7),
    CancelOrderRequest(true, 8),
    GetPriceHistoryRequest(true, 9),

    // Response kind
    NullResponse(false, 0),
    RegisterResponse(false, 1),
    UpdateCredentialsResponse(false, 2),
    LoginResponse(false, 3),
    LogoutResponse(false, 4),
    InsertLimitOrderResponse(false, 5),
    InsertMarketOrderResponse(false, 6),
    InsertStopOrderResponse(false, 7),
    CancelOrderResponse(false, 8),
    GetPriceHistoryResponse(false, 9);

    private final boolean _isRequest;
    private final int _code;

    MessageKind(boolean isRequest, int code)
    {
        _isRequest = isRequest;
        _code = code;
    }

    public boolean IsRequest() { return _isRequest; }
    public boolean IsResponse() { return !_isRequest; }


    /*
     *        xxxxxxxx_00000000_00000000_00000000
     *       |        |
     *        control part. Only specify if is a request or a response message atm.
     *
     *        00000000_xxxxxxxx_xxxxxxxx_xxxxxxxx
     *                |                          |
     *        message type. Specifies the type of the message.
     * */
    public int ToInt()
    {
        if (_isRequest) { return _code | (1 << 31); }
        else { return _code; }
    }

    public static MessageKind FromInt(int value)
    {
        int code = value & 0b01111111_11111111_11111111_11111111;

        if (value < 0)
        {
            return switch (code)
            {
                case 1 -> RegisterRequest;
                case 2 -> UpdateCredentialsRequest;
                case 3 -> LoginRequest;
                case 4 -> LogoutRequest;
                case 5 -> InsertLimitOrderRequest;
                case 6 -> InsertMarketOrderRequest;
                case 7 -> InsertStopOrderRequest;
                case 8 -> CancelOrderRequest;
                case 9 -> GetPriceHistoryRequest;
                default -> NullRequest;
            };
        }
        else
        {
            return switch (code)
            {
                case 1 -> RegisterResponse;
                case 2 -> UpdateCredentialsResponse;
                case 3 -> LoginResponse;
                case 4 -> LogoutResponse;
                case 5 -> InsertLimitOrderResponse;
                case 6 -> InsertMarketOrderResponse;
                case 7 -> InsertStopOrderResponse;
                case 8 -> CancelOrderResponse;
                case 9 -> GetPriceHistoryResponse;
                default -> NullResponse;
            };
        }
    }
}