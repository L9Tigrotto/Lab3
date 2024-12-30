
package Messages;

public class SimpleResponse
{
    private final int _response;
    private final String _errorMessage;

    public SimpleResponse(int response, String errorMessage)
    {
        _response = response;
        _errorMessage = errorMessage;
    }

    public int GetResponse() { return _response; }
    public String GetErrorMessage() { return _errorMessage; }

    public String Serialize()
    {
        return "";
    }

    public static SimpleResponse Deserialize()
    {
        return null;
    }
}
