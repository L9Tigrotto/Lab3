
package Messages;

/*
*        xxxxxxxx_00000000_00000000_00000000
*       |        |
*        control part. Only specify if is a request or a response message atm.
*
*        00000000_xxxxxxxx_xxxxxxxx_xxxxxxxx
*                |                          |
*        message type. Specifies the type of the message.
* */

public enum MessageKind
{
    // Request kind
    RegisterRequest(0b00000001_00000000_00000000_00000001),
    UpdateCredentialsRequest(0b00000001_00000000_00000000_00000010),
    LoginRequest(0b00000001_00000000_00000000_00000100),
    LogoutRequest(0b00000001_00000000_00000000_00001000),
    InsertLimitOrderRequest(0b00000001_00000000_00000000_00010000),
    InsertMarketOrderRequest(0b00000001_00000000_00000000_00100000),
    InsertStopOrderRequest(0b00000001_00000000_00000000_01000000),
    CancelOrderRequest(0b00000001_00000000_00000000_10000000),
    GetPriceHistoryRequest(0b00000001_00000000_00000001_00000000),

    // Response kind
    RegisterResponse(0b00000010_00000000_00000000_00000001),
    UpdateCredentialsResponse(0b00000010_00000000_00000000_00000010),
    LoginResponse(0b00000010_00000000_00000000_00000100),
    LogoutResponse(0b00000010_00000000_00000000_00001000),
    InsertLimitOrderResponse(0b00000010_00000000_00000000_00010000),
    InsertMarketOrderResponse(0b00000010_00000000_00000000_00100000),
    InsertStopOrderResponse(0b00000010_00000000_00000000_01000000),
    CancelOrderResponse(0b00000010_00000000_00000000_10000000),
    GetPriceHistoryResponse(0b00000010_00000000_00000001_00000000);

    // Control
    private static final int REQUEST_FLAG = 0b00000001_00000000_00000000_00000000;
    private static final int RESPONSE_FLAG = 0b00000010_00000000_00000000_00000000;

    // Type
    private static final int REGISTER_FLAG = 0b00000000_00000000_00000000_00000001;
    private static final int UPDATE_CREDENTIAL_FLAG = 0b00000000_00000000_00000000_00000010;
    private static final int LOGIN_FLAG = 0b00000000_00000000_00000000_00000100;
    private static final int LOGOUT_FLAG = 0b00000000_00000000_00000000_00001000;
    private static final int INSERT_LIMIT_ORDER_FLAG = 0b00000000_00000000_00000000_00010000;
    private static final int INSERT_MARKET_ORDER_FLAG = 0b00000000_00000000_00000000_00100000;
    private static final int INSERT_STOP_ORDER_FLAG = 0b00000000_00000000_00000000_01000000;
    private static final int CANCEL_ORDER_FLAG = 0b00000000_00000000_00000000_10000000;
    private static final int GET_PRICE_HISTORY_FLAG = 0b00000000_00000000_00000001_00000000;

    private final int _code;
    MessageKind(int code) { _code = code; }

    public boolean IsRequest() { return (_code & REQUEST_FLAG) != 0; }
    public boolean IsResponse() { return (_code & RESPONSE_FLAG) != 0; }

    public static MessageKind FromInt(int code)
    {
        return switch (code)
        {
            case 0b00000001_00000000_00000000_00000001 -> RegisterRequest;
            case 0b00000001_00000000_00000000_00000010 -> UpdateCredentialsRequest;
            case 0b00000001_00000000_00000000_00000100 -> LoginRequest;
            case 0b00000001_00000000_00000000_00001000 -> LogoutRequest;
            case 0b00000001_00000000_00000000_00010000 -> InsertLimitOrderRequest;
            case 0b00000001_00000000_00000000_00100000 -> InsertMarketOrderRequest;
            case 0b00000001_00000000_00000000_01000000 -> InsertStopOrderRequest;
            case 0b00000001_00000000_00000000_10000000 -> CancelOrderRequest;
            case 0b00000001_00000000_00000001_00000000 -> GetPriceHistoryRequest;

            case 0b00000010_00000000_00000000_00000001 -> RegisterResponse;
            case 0b00000010_00000000_00000000_00000010 -> UpdateCredentialsResponse;
            case 0b00000010_00000000_00000000_00000100 -> LoginResponse;
            case 0b00000010_00000000_00000000_00001000 -> LogoutResponse;
            case 0b00000010_00000000_00000000_00010000 -> InsertLimitOrderResponse;
            case 0b00000010_00000000_00000000_00100000 -> InsertMarketOrderResponse;
            case 0b00000010_00000000_00000000_01000000 -> InsertStopOrderResponse;
            case 0b00000010_00000000_00000000_10000000 -> CancelOrderResponse;
            case 0b00000010_00000000_00000001_00000000 -> GetPriceHistoryResponse;

            default -> throw new IllegalArgumentException("Invalid code");
        };
    }
}