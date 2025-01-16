
package Users;

import Messages.*;

/**
 * Represents a user with a username and password, providing functionality
 * to check user credentials and update passwords.
 */
public class User
{
    private final String _username;
    private String _password;

    /**
     * Constructor to initialize the User object with a username and password.
     *
     * @param name the username of the user
     * @param password the password of the user
     */
    public User(String name, String password)
    {
        _username = name;
        _password = password;
    }

    /**
     * Gets the username of the user.
     *
     * @return the username
     */
    public String GetUsername() { return _username; }

    /**
     * Gets the password of the user.
     *
     * @return the password
     */
    public String GetPassword() { return _password; }

    /**
     * Compares the current password with a provided one to check if they match.
     * This method is synchronized to prevent race conditions during password checks.
     *
     * @param newPassword the password to check
     * @return true if the passwords match, false otherwise
     */
    public boolean MatchPassword(String newPassword)
    {
        synchronized (this)
        {
            return _password.equals(newPassword);
        }
    }

    /**
     * Tries to update the user's password. This method ensures that:
     * 1. The old password matches the current password.
     * 2. The new password is different from the old one.
     * 3. The user is not currently logged in from another session.
     *
     * @param oldPassword the current password of the user
     * @param newPassword the new password to set
     * @return a response indicating the outcome of the password update attempt
     */
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
            if (UserCollection.IsConnected(_username)) { return UpdateCredentialsRequest.USER_LOGGED_IN; }

            // update the user's password with the new password
            _password = newPassword;
        }

        // return a response indicating the password was successfully updated
        return UpdateCredentialsRequest.OK;
    }

    /**
     * Validates the username based on its length (minimum 3 characters).
     *
     * @param username the username to validate
     * @return true if the username is valid, false otherwise
     */
    public static boolean IsUsernameValid(String username) { return username.length() >= 3; }

    /**
     * Validates the password based on its length (minimum 3 characters).
     *
     * @param password the password to validate
     * @return true if the password is valid, false otherwise
     */
    public static boolean IsPasswordValid(String password) { return password.length() >= 3; }
}
