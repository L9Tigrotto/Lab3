
package Messages;

public class RegisterRequest
{
    private final String _username;
    private final String _password;

    public RegisterRequest(String username, String password)
    {
        _username = username;
        _password = password;
    }

    public String GetUsername() { return _username; }
    public String GetPassword() { return _password; }

    public Message ToMessage()
    {
        String data = Message.JSON_BUILDER.toJson(this);
        return new Message(MessageKind.RegisterRequest, data);
    }

    public static RegisterRequest FromMessage(Message message)
    {
        return Message.JSON_BUILDER.fromJson(message.GetData(), RegisterRequest.class);
    }
}
