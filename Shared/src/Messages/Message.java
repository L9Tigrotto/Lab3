
package Messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Message
{
    public static final Gson JSON_BUILDER = new GsonBuilder().setPrettyPrinting().create();

    private final MessageKind _kind;
    private final String _data;

    public Message(MessageKind kind, String data)
    {
        _kind = kind;
        _data = data;
    }

    public MessageKind GetKind() { return _kind; }
    public String GetData() { return _data; }
}
