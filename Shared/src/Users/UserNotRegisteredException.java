
package Users;

/**
 * Custom exception class to represent the error when a user is not registered.
 * This exception can be thrown when attempting to perform an operation on a user
 * that does not exist or has not been registered in the system.
 */
public class UserNotRegisteredException extends Exception
{
    public UserNotRegisteredException() { super(); }
}
