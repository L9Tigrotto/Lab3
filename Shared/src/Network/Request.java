
package Network;

import Messages.RegisterRequest;
import com.google.gson.FormattingStyle;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class Request
{
    public static String ToJson(ITransmittable transmittable)
    {
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = new JsonWriter(stringWriter);)
        {
            jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
            jsonWriter.beginObject();

            jsonWriter.name("operation").value(transmittable.GetOperation());
            jsonWriter.name("values");
            jsonWriter.beginObject();
            transmittable.ToJson(jsonWriter);
            jsonWriter.endObject();

            jsonWriter.endObject();
        } catch (IOException e) { throw new RuntimeException(e); }

        return stringWriter.toString();
    }

    public static ITransmittable FromJson(String json) throws IOException
    {
        String temp;
        String operation;
        ITransmittable transmittable;

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
                case "register" -> transmittable = RegisterRequest.FromJson(jsonReader);
                default -> throw new IOException("Supposed to read a valid transmittable name from JSON (got " + temp + ")");
            }

            jsonReader.endObject();

            jsonReader.endObject();
        }

        return transmittable;
    }
}
