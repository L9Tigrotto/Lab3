
package Networking;

import Messages.OrderResponse;
import Messages.SimpleResponse;
import com.google.gson.FormattingStyle;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * This abstract class represents a network response in JSON format.
 * It defines the common structure for responses and provides a foundation for creating
 * specific types of responses. Subclasses are responsible for serializing and deserializing
 * the content specific to their type of response.
 */
public abstract class Response
{
    public Response() { }

    /**
     * Converts the response object to a well-formatted JSON string.
     * The specific format of the JSON string is determined by the implementation of
     * the `SerializeContent` method in subclasses.
     *
     * This method serializes the response content into a JSON structure, making use of
     * the Gson library to format the output in a readable (pretty) style.
     *
     * @return The JSON string representation of the response object.
     * @throws IOException If an error occurs during JSON serialization.
     */
    public String ToJson() throws IOException
    {
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = new JsonWriter(stringWriter);)
        {
            jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
            jsonWriter.beginObject();
            SerializeContent(jsonWriter);
            jsonWriter.endObject();
        }

        return stringWriter.toString();
    }

    /**
     * Abstract method that must be implemented by subclasses to serialize their
     * specific content into the JSON object.
     * This method is called within the `ToJson` method to serialize the details
     * specific to each response type.
     *
     * @param jsonWriter The JsonWriter object used for serializing the content into JSON.
     * @throws IOException If an error occurs during JSON serialization.
     */
    protected abstract void SerializeContent(JsonWriter jsonWriter) throws IOException;

    /**
     * Parses a JSON string representing a network response and returns the
     * corresponding Response object.
     * This static method determines the type of response based on the keys present
     * in the JSON. It currently supports "response" and "orderID" keys, which are used
     * to identify `SimpleResponse` and `OrderResponse` types respectively.
     *
     * @param json The JSON string representing the network response.
     * @return The Response object corresponding to the JSON string.
     * @throws IOException If an error occurs during JSON deserialization.
     */
    public static Response FromJson(String json) throws IOException
    {
        String temp;
        Response response;

        try (StringReader stringReader = new StringReader(json);
             JsonReader jsonReader = new JsonReader(stringReader))
        {
            jsonReader.beginObject();

            // read the first field name to determine the type of response
            temp = jsonReader.nextName();

            // based on the field name, deserialize into the correct type of response
            switch (temp)
            {
                case "response" -> response = SimpleResponse.FromJson(jsonReader);
                case "orderID" -> response = OrderResponse.FromJson(jsonReader);
                default -> throw new IOException("Supposed to read a valid transmittable name from JSON (got " + temp + ")");
            }

            jsonReader.endObject();
        }

        return response;
    }
}
