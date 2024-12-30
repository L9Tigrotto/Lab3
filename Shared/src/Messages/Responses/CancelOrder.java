
package Messages.Responses;

import Messages.SimpleResponse;

public class CancelOrder extends SimpleResponse
{
    public CancelOrder(int response, String errorMessage)
    {
        super(response, errorMessage);
    }
}
