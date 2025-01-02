
package Messages;

public class RegisterMessage
{
    private final String _username;
    private final String _password;
    private transient boolean _isRequest;

    public RegisterMessage(String username, String password, boolean isRequest)
    {
        _username = username;
        _password = password;
        _isRequest = isRequest;
    }

    public RegisterMessage(String username, String password)
    {
        this(username, password, true);
    }

    public Message ToMessage(boolean isRequest)
    {
        String data = Message.JSON_BUILDER.toJson(this);
        if (isRequest) { return new Message(MessageKind.RegisterRequest, data); }
        else { return new Message(MessageKind.RegisterResponse, data); }
    }

    public static RegisterMessage FromMessage(Message message)
    {
        RegisterMessage registerMessage = Message.JSON_BUILDER.fromJson(message.GetData(), RegisterMessage.class);
        registerMessage._isRequest = message.GetKind().IsRequest();
        return registerMessage;
    }
}
