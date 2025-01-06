package Network;

import Messages.SimpleResponse;
import com.google.gson.FormattingStyle;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class Response
{
    public static String ToJson(ITransmittable transmittable)
    {
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = new JsonWriter(stringWriter);)
        {
            jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
            jsonWriter.beginObject();
            transmittable.ToJson(jsonWriter);
            jsonWriter.endObject();
        } catch (IOException e) { throw new RuntimeException(e); }

        return stringWriter.toString();
    }

    public static ITransmittable FromJson(String json) throws IOException
    {
        String temp;
        ITransmittable transmittable;

        try (StringReader stringReader = new StringReader(json);
             JsonReader jsonReader = new JsonReader(stringReader))
        {
            jsonReader.beginObject();

            temp = jsonReader.nextName();

            switch (temp)
            {
                case "response" -> transmittable = SimpleResponse.FromJson(jsonReader);
                case "orderID" -> transmittable = SimpleResponse.FromJson(jsonReader);
                default -> throw new IOException("Supposed to read a valid transmittable name from JSON (got " + temp + ")");
            }

            jsonReader.endObject();
        }

        return transmittable;
    }
}
