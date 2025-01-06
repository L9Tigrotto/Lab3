
package Network;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public interface ITransmittable
{
    public String GetOperation();

    public void ToJson(JsonWriter jsonWriter) throws IOException;
}
