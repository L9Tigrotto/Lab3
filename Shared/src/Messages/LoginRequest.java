
package Messages;

import Helpers.Utilities;
import Networking.OperationType;
import Networking.Request;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class LoginRequest extends Request
{
    public static final SimpleResponse OK = new SimpleResponse(100, "OK");
    public static final SimpleResponse USERNAME_PASSWORD_MISMATCH = new SimpleResponse(101, "Username/Password mismatch or non existent user");
    public static final SimpleResponse NON_EXISTENT_USER = new SimpleResponse(101, "Username/Password mismatch or non existent user");
    public static final SimpleResponse USER_ALREADY_LOGGED_IN = new SimpleResponse(102, "User already logged in");
    public static final SimpleResponse OTHER_ERROR_CASES = new SimpleResponse(103, "Other error cases");

    private final String _username;
    private final String _password;

    public LoginRequest(String username, String password)
    {
        super(OperationType.LOGIN);
        _username = username;
        _password = password;
    }

    public String GetUsername() { return _username; }
    public String GetPassword() { return _password; }

    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("username").value(_username);
        jsonWriter.name("password").value(_password);
    }

    public static LoginRequest DeserializeContent(JsonReader jsonReader) throws IOException
    {
        String username = Utilities.ReadString(jsonReader, "username");
        String password = Utilities.ReadString(jsonReader, "password");

        return new LoginRequest(username, password);
    }
}
