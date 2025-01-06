
package Messages;

import Network.ITransmittable;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class SimpleResponse implements ITransmittable
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

    public String GetOperation() { return ""; }

    public void ToJson(JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("response").value(_response);
        jsonWriter.name("errorMessage").value(_errorMessage);
    }

    public static SimpleResponse FromJson(JsonReader jsonReader) throws IOException
    {
        int response = jsonReader.nextInt();

        String temp = jsonReader.nextName();
        if (!temp.equals("errorMessage")) { throw new IOException("Supposed to read 'errorMessage' from JSON (got " + temp + ")"); }
        String errorMessage = jsonReader.nextString();

        return new SimpleResponse(response, errorMessage);
    }
}
