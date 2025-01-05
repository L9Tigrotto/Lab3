
package Network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Message
{
    public static final Gson JSON_BUILDER = new GsonBuilder().setPrettyPrinting().create();

    private final MessageType _type;
    private final String _data;

    public Message(MessageType kind, String data)
    {
        _type = kind;
        _data = data;
    }

    public MessageType GetType() { return _type; }
    public String GetData() { return _data; }
}
