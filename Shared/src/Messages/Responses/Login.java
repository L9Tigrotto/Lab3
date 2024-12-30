
package Messages.Responses;

import Messages.SimpleResponse;

public class Login extends SimpleResponse
{
    public Login(int response, String errorMessage)
    {
        super(response, errorMessage);
    }
}
