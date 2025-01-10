
package Users;

import Messages.*;

public class User
{
    private final String _username;
    private String _password;
    private boolean _isConnected;

    public User(String name, String password)
    {
        _username = name;
        _password = password;
        _isConnected = false;
    }

    public String GetUsername() { return _username; }
    public String GetPassword() { return _password; }
    public boolean IsConnected() { return _isConnected; }

    public boolean MatchPassword(String newPassword) { return _password.equals(newPassword); }

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
            if (_isConnected) { return UpdateCredentialsRequest.USER_LOGGED_IN; }

            // update the user's password with the new password
            _password = newPassword;
        }

        return UpdateCredentialsRequest.OK;
    }

    public SimpleResponse TryLogIn(String password)
    {
        synchronized (this)
        {
            // verify that the provided password matches the user's stored password
            if (!MatchPassword(password)) { return LoginRequest.USERNAME_PASSWORD_MISMATCH; }

            // check if the user is already logged in from another session
            if (_isConnected) { return LoginRequest.USER_ALREADY_LOGGED_IN; }

            // mark the user as connected
            _isConnected = true;
        }

        return LoginRequest.OK;
    }

    public SimpleResponse TryLogout()
    {
        synchronized (this)
        {
            // check if the user is already logged out and set the connected flag to false
            if (!_isConnected) { return LogoutRequest.USER_NOT_LOGGED; }
            _isConnected = false;
        }

        return LogoutRequest.OK;
    }

    public static boolean IsUsernameValid(String username) { return username.length() >= 3; }
    public static boolean IsPasswordValid(String password) { return password.length() >= 3; }
}
