
package Messages.Responses;

import Messages.SimpleResponse;

public class Logout extends SimpleResponse
{
    public Logout(int response, String errorMessage)
    {
        super(response, errorMessage);
    }
}
