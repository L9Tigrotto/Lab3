
package Messages;

import Network.ITransmittable;
import Network.Message;
import Network.MessageType;

public class SimpleResponse implements ITransmittable
{
    private final int _response;
    private final String _errorMessage;

    public SimpleResponse(int response, String errorMessage)
    {
        _response = response;
        _errorMessage = errorMessage;
    }

    public int GetResponse() { return _response; }
    public String GetErrorMessage() { return _errorMessage; }

    public Message ToMessage()
    {
        String data = Message.JSON_BUILDER.toJson(this);
        return new Message(MessageType.RegisterRequest, data);
    }

    public static SimpleResponse FromMessage(Message message)
    {
        return Message.JSON_BUILDER.fromJson(message.GetData(), SimpleResponse.class);
    }
}
