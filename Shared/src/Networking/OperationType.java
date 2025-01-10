package Networking;

public enum OperationType
{
    REGISTER(0),
    UPDATE_CREDENTIALS(1),
    LOGIN(2),
    LOGOUT(3);

    private final int _value;
    OperationType(int value)
    {
        _value = value;
    }

    public String ToString()
    {
        return switch (this)
        {
            case REGISTER -> "register";
            case UPDATE_CREDENTIALS -> "ask";
            case LOGIN -> "ask";
            case LOGOUT -> "ask";
            default -> "unknown";
        };
    }
}
