
package Messages;

import Networking.Request;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class UpdateCredentialsRequest  extends Request
{
    public static final SimpleResponse OK = new SimpleResponse(100, "OK");
    public static final SimpleResponse INVALID_NEWPASSWORD = new SimpleResponse(101, "Invalid new password");
    public static final SimpleResponse USERNAME_OLDPASSWORD_MISMATCH = new SimpleResponse(102, "Username/old_password mismatch or non existent username");
    public static final SimpleResponse NON_EXISTENT_USER = new SimpleResponse(102, "Username/old_password mismatch or non existent username");
    public static final SimpleResponse NEW_AND_OLD_PASSWORD_EQUAL = new SimpleResponse(103, "New password equal to old one");
    public static final SimpleResponse USER_LOGGED_IN = new SimpleResponse(104, "User currently logged in");
    public static final SimpleResponse OTHER_ERROR_CASES = new SimpleResponse(105, "Other error cases");

    private final String _username;
    private final String _oldPassword;
    private final String _newPassword;

    public UpdateCredentialsRequest(String username, String oldPassword, String newPassword)
    {
        super("updateCredentials");
        _username = username;
        _oldPassword = oldPassword;
        _newPassword = newPassword;
    }

    public String GetUsername() { return _username; }
    public String GetOldPassword() { return _oldPassword; }
    public String GetNewPassword() { return _newPassword; }

    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("username").value(_username);
        jsonWriter.name("oldPassword").value(_oldPassword);
        jsonWriter.name("newPassword").value(_newPassword);
    }

    public static UpdateCredentialsRequest DeserializeContent(JsonReader jsonReader) throws IOException
    {
        // read the "username" field
        String temp = jsonReader.nextName();
        if (!temp.equals("username")) { throw new IOException("Supposed to read 'username' from JSON (got " + temp + ")"); }
        String username = jsonReader.nextString();

        // read the "oldPassword" field
        temp = jsonReader.nextName();
        if (!temp.equals("oldPassword")) { throw new IOException("Supposed to read 'oldPassword' from JSON (got " + temp + ")"); }
        String oldPassword = jsonReader.nextString();

        // read the "newPassword" field
        temp = jsonReader.nextName();
        if (!temp.equals("newPassword")) { throw new IOException("Supposed to read 'newPassword' from JSON (got " + temp + ")"); }
        String newPassword = jsonReader.nextString();

        return new UpdateCredentialsRequest(username, oldPassword, newPassword);
    }
}
