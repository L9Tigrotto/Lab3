
package Messages;

import Networking.OperationType;
import Networking.Request;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class LogoutRequest extends Request
{
    public static final SimpleResponse OK = new SimpleResponse(100, "OK");
    public static final SimpleResponse USER_NOT_LOGGED = new SimpleResponse(101, "Username/connection mismatch or non existent user or user not logged in or other error cases");
    public static final SimpleResponse OTHER_ERROR_CASES = new SimpleResponse(101, "Username/connection mismatch or non existent user or user not logged in or other error cases");

    public LogoutRequest() { super(OperationType.LOGOUT); }

    protected void SerializeContent(JsonWriter jsonWriter) throws IOException { }

    public static LogoutRequest DeserializeContent(JsonReader jsonReader) throws IOException { return new LogoutRequest(); }
}
