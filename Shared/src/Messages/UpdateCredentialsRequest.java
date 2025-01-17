
package Messages;

import Networking.OperationType;
import Networking.Request;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * This class represents a request to update the credentials (username and password) of a user.
 * It extends the `Request` class and handles the serialization and deserialization of the request
 * content to and from JSON.
 */
public class UpdateCredentialsRequest  extends Request
{
    // response codes that can be returned for this request
    public static final SimpleResponse OK = new SimpleResponse(100, "OK");
    public static final SimpleResponse INVALID_NEWPASSWORD = new SimpleResponse(101, "Invalid new password");
    public static final SimpleResponse USERNAME_OLDPASSWORD_MISMATCH = new SimpleResponse(102, "Username/old_password mismatch or non existent username");
    public static final SimpleResponse NON_EXISTENT_USER = new SimpleResponse(102, "Username/old_password mismatch or non existent username");
    public static final SimpleResponse NEW_AND_OLD_PASSWORD_EQUAL = new SimpleResponse(103, "New password equal to old one");
    public static final SimpleResponse USER_LOGGED_IN = new SimpleResponse(104, "User currently logged in");
    public static final SimpleResponse OTHER_ERROR_CASES = new SimpleResponse(105, "Other error cases");

    // fields for the request data: username, old password, and new password
    private final String _username;
    private final String _oldPassword;
    private final String _newPassword;

    /**
     * Constructor to initialize the UpdateCredentialsRequest with the user's username,
     * old password, and new password.
     *
     * @param username The username for which the credentials need to be updated.
     * @param oldPassword The user's old password.
     * @param newPassword The new password to set for the user.
     */
    public UpdateCredentialsRequest(String username, String oldPassword, String newPassword)
    {
        super(OperationType.UPDATE_CREDENTIALS);
        _username = username;
        _oldPassword = oldPassword;
        _newPassword = newPassword;
    }

    /**
     * Getter for the username.
     *
     * @return The username whose credentials are being updated.
     */
    public String GetUsername() { return _username; }

    /**
     * Getter for the old password.
     *
     * @return The old password associated with the username.
     */
    public String GetOldPassword() { return _oldPassword; }

    /**
     * Getter for the new password.
     *
     * @return The new password that the user wants to set.
     */
    public String GetNewPassword() { return _newPassword; }

    /**
     * Serializes the content of this request into JSON format.
     * The JSON will contain the "username", "oldPassword", and "newPassword" fields.
     *
     * @param jsonWriter The JSON writer used for serialization.
     * @throws IOException If an I/O error occurs during serialization.
     */
    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("username").value(_username);
        jsonWriter.name("oldPassword").value(_oldPassword);
        jsonWriter.name("newPassword").value(_newPassword);
    }

    /**
     * Deserializes an UpdateCredentialsRequest from JSON format.
     * The method expects the JSON to contain "username", "oldPassword", and "newPassword" fields.
     *
     * @param jsonReader The JSON reader used for deserialization.
     * @return A new UpdateCredentialsRequest instance with the deserialized data.
     * @throws IOException If an I/O error occurs during deserialization, or if the expected fields are not found.
     */
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
