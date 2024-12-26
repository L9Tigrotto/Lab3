package Shared.DataStructures.Messages;

public class Message
{
    private final Kind _kind;
    private final String _data;

    public Message(Kind kind, String data)
    {
        _kind = kind;
        _data = data;
    }

    public Kind GetKind() { return _kind; }
    public String GetData() { return _data; }
}
