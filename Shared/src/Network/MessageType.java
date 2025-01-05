
package Network;

public enum MessageType
{
    // Request kind
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
    SimpleResponse(false, 1),
    OrderResponse(false, 2);

    private final boolean _isRequest;
    private final int _code;

    MessageType(boolean isRequest, int code) {
        _isRequest = isRequest;
        _code = code;
    }

    public boolean IsRequest() {
        return _isRequest;
    }

    public boolean IsResponse() {
        return !_isRequest;
    }


    /*
     *  xxxxxxxx_00000000_00000000_00000000
     * |        |
     *  control part. Only specify if is a request or a response message atm.
     *
     *  00000000_xxxxxxxx_xxxxxxxx_xxxxxxxx
     *          |                          |
     *  message type. Specifies the type of the message.
     * */
    public int ToInt() {
        if (_isRequest) {
            return _code | (1 << 31);
        } else {
            return _code;
        }
    }

    public static MessageType FromInt(int value) {
        int code = value & 0b01111111_11111111_11111111_11111111;

        if (value < 0) {
            return switch (code) {
                case 1 -> RegisterRequest;
                case 2 -> UpdateCredentialsRequest;
                case 3 -> LoginRequest;
                case 4 -> LogoutRequest;
                case 5 -> InsertLimitOrderRequest;
                case 6 -> InsertMarketOrderRequest;
                case 7 -> InsertStopOrderRequest;
                case 8 -> CancelOrderRequest;
                case 9 -> GetPriceHistoryRequest;
                default -> throw new Error("Unknown code " + code);
            };
        } else {
            return switch (code) {
                case 1 -> SimpleResponse;
                case 2 -> OrderResponse;
                default -> throw new Error("Unknown code " + code);
            };
        }
    }
}