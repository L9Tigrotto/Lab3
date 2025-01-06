
package Messages;

import Network.ITransmittable;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class RegisterRequest implements ITransmittable
{
    public static final SimpleResponse OK_RESPONSE = new SimpleResponse(100, "OK");
    public static final SimpleResponse INVALID_PASSWORD_RESPONSE = new SimpleResponse(101, "Invalid Password");
    public static final SimpleResponse USERNAME_NOT_AVAILABLE_RESPONSE = new SimpleResponse(102, "Username not available");
    public static final SimpleResponse OTHER_ERROR_CASES_RESPONSE = new SimpleResponse(103, "Other error cases");

    private final String _username;
    private final String _password;

    public RegisterRequest(String username, String password)
    {
        _username = username;
        _password = password;
    }

    public String GetUsername() { return _username; }
    public String GetPassword() { return _password; }
    public boolean IsPasswordValid() { return _password.length() >= 3; }
    public boolean IsUsernameValid() { return _username.length() >= 3; }

    public String GetOperation() { return "register"; }

    public void ToJson(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("username").value(_username);
        jsonWriter.name("password").value(_password);
    }

    public static RegisterRequest FromJson(JsonReader jsonReader) throws IOException
    {
        String temp = jsonReader.nextName();
        if (!temp.equals("username")) { throw new IOException("Supposed to read 'username' from JSON (got " + temp + ")"); }
        String username = jsonReader.nextString();

        temp = jsonReader.nextName();
        if (!temp.equals("password")) { throw new IOException("Supposed to read 'password' from JSON (got " + temp + ")"); }
        String password = jsonReader.nextString();

        return new RegisterRequest(username, password);
    }
}
