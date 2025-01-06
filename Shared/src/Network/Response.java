package Network;

import Messages.SimpleResponse;
import com.google.gson.FormattingStyle;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public abstract class Response
{
    public Response() { }

    public String ToJson()
    {
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = new JsonWriter(stringWriter);)
        {
            jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
            jsonWriter.beginObject();
            SerializeContent(jsonWriter);
            jsonWriter.endObject();
        } catch (IOException e) { throw new RuntimeException(e); }

        return stringWriter.toString();
    }

    protected abstract void SerializeContent(JsonWriter jsonWriter) throws IOException;

    public static Response FromJson(String json) throws IOException
    {
        String temp;
        Response response;

        try (StringReader stringReader = new StringReader(json);
             JsonReader jsonReader = new JsonReader(stringReader))
        {
            jsonReader.beginObject();

            temp = jsonReader.nextName();

            switch (temp)
            {
                case "response" -> response = SimpleResponse.FromJson(jsonReader);
                case "orderID" -> response = SimpleResponse.FromJson(jsonReader);
                default -> throw new IOException("Supposed to read a valid transmittable name from JSON (got " + temp + ")");
            }

            jsonReader.endObject();
        }

        return response;
    }
}
