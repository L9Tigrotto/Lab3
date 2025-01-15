
package Users;

import Messages.*;

public class User
{
    private final String _username;
    private String _password;

    public User(String name, String password)
    {
        _username = name;
        _password = password;
    }

    public String GetUsername() { return _username; }
    public String GetPassword() { return _password; }
    public boolean IsConnected() { return UserCollection.IsConnected(_username); }

    public boolean MatchPassword(String newPassword)
    {
        synchronized (this)
        {
            return _password.equals(newPassword);
        }
    }

    public SimpleResponse TryUpdatePassword(String oldPassword, String newPassword)
    {
        // synchronize this block to prevent race conditions when multiple requests attempt to modify the
        // same user concurrently
        synchronized (this)
        {
            // verify that the provided old password matches the user's current password
            if (!MatchPassword(oldPassword)) { return UpdateCredentialsRequest.USERNAME_OLDPASSWORD_MISMATCH; }

            // prevent the user from setting the new password to the same as the old password
            if (MatchPassword(newPassword)) { return UpdateCredentialsRequest.NEW_AND_OLD_PASSWORD_EQUAL; }

            // specifically checks if the target user is connected to another session, not just whether any user is
            // connected to this particular Networking.ClientHandler instance
            if (IsConnected()) { return UpdateCredentialsRequest.USER_LOGGED_IN; }

            // update the user's password with the new password
            _password = newPassword;
        }

        return UpdateCredentialsRequest.OK;
    }

    public static boolean IsUsernameValid(String username) { return username.length() >= 3; }
    public static boolean IsPasswordValid(String password) { return password.length() >= 3; }
}
