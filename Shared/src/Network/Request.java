
package Network;

import Messages.RegisterRequest;
import com.google.gson.FormattingStyle;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public abstract class Request
{
    private final String _operation;

    public Request(String operation)
    {
        _operation = operation;
    }

    public String GetOperation() { return _operation; }

    public String ToJson()
    {
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = new JsonWriter(stringWriter);)
        {
            jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
            jsonWriter.beginObject();

            jsonWriter.name("operation").value(_operation);
            jsonWriter.name("values");
            jsonWriter.beginObject();
            SerializeContent(jsonWriter);
            jsonWriter.endObject();

            jsonWriter.endObject();
        } catch (IOException e) { throw new RuntimeException(e); }

        return stringWriter.toString();
    }

    protected abstract void SerializeContent(JsonWriter jsonWriter) throws IOException;

    public static Request FromJson(String json) throws IOException
    {
        String temp;
        String operation;
        Request request;

        try (StringReader stringReader = new StringReader(json);
            JsonReader jsonReader = new JsonReader(stringReader))
        {
            jsonReader.beginObject();

            temp = jsonReader.nextName();
            if (!temp.equals("operation")) { throw new IOException("Supposed to read 'operation' name from JSON (got " + temp + ")"); }

            operation = jsonReader.nextString();

            temp = jsonReader.nextName();
            if (!temp.equals("values")) { throw new IOException("Supposed to read 'values' name from JSON (got " + temp + ")"); }
            jsonReader.beginObject();

            switch (operation)
            {
                case "register" -> request = RegisterRequest.DeserializeContent(jsonReader);
                default -> throw new IOException("Supposed to read a valid transmittable name from JSON (got " + temp + ")");
            }

            jsonReader.endObject();

            jsonReader.endObject();
        }

        return request;
    }
}
