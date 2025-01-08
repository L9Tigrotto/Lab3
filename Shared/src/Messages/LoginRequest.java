
package Messages;

import Network.Request;
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
        super("login");
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
        // read the "username" field
        String temp = jsonReader.nextName();
        if (!temp.equals("username")) { throw new IOException("Supposed to read 'username' from JSON (got " + temp + ")"); }
        String username = jsonReader.nextString();

        // read the "password" field
        temp = jsonReader.nextName();
        if (!temp.equals("password")) { throw new IOException("Supposed to read 'password' from JSON (got " + temp + ")"); }
        String password = jsonReader.nextString();

        return new LoginRequest(username, password);
    }
}
