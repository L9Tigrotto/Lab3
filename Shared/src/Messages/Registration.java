
package Messages;

import Network.ITransmittable;
import Network.Message;
import Network.MessageType;

public class Registration implements ITransmittable
{
    public static final SimpleResponse OK_RESPONSE = new SimpleResponse(100, "OK");
    public static final SimpleResponse INVALID_PASSWORD_RESPONSE = new SimpleResponse(101, "Invalid Password");
    public static final SimpleResponse USERNAME_NOT_AVAILABLE_RESPONSE = new SimpleResponse(102, "Username not available");
    public static final SimpleResponse OTHER_ERROR_CASES_RESPONSE = new SimpleResponse(103, "Other error cases");

    private final String _username;
    private final String _password;

    public Registration(String username, String password)
    {
        _username = username;
        _password = password;
    }

    public String GetUsername() { return _username; }
    public String GetPassword() { return _password; }
    public boolean IsPasswordValid() { return _password.length() > 3; }
    public boolean IsUsernameValid() { return _password.length() > 3; }

    public Message ToMessage()
    {
        String data = Message.JSON_BUILDER.toJson(this);
        return new Message(MessageType.RegisterRequest, data);
    }

    public static Registration FromMessage(Message message)
    {
        return Message.JSON_BUILDER.fromJson(message.GetData(), Registration.class);
    }
}
