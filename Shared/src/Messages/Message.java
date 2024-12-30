
package Messages;

public class Message
{
    private final MessageKind _kind;
    private final String _serializedData;

    public Message(MessageKind kind, String data)
    {
        _kind = kind;
        _serializedData = data;
    }

    public MessageKind GetKind() { return _kind; }
    public String GetData() { return _serializedData; }
}
