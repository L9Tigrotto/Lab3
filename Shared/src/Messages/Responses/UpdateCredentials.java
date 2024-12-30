
package Messages.Responses;

import Messages.SimpleResponse;

public class UpdateCredentials extends SimpleResponse
{
    public UpdateCredentials(int response, String errorMessage)
    {
        super(response, errorMessage);
    }
}
