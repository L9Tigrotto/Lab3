package Messages;

public class TextMessage
{
    private final MessageKind _kind;
    private final String _text;

    public TextMessage(MessageKind kind, String text)
    {
        _kind = kind;
        _text = text;
    }

    public boolean IsRequest() { return _kind.IsRequest(); }
    public boolean IsResponse() { return _kind.IsResponse(); }
    public String GetText() { return _text; }

    public Message ToMessage()
    {
        return new Message(_kind, _text);
    }
}
