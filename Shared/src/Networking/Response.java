
package Networking;

import Messages.OrderResponse;
import Messages.SimpleResponse;
import Orders.Order;
import com.google.gson.FormattingStyle;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * This abstract class defines a foundation for network responses in JSON format.
 * It provides a common structure for responses where subclasses can serialize
 * their specific content into a JSON object.
 */
public abstract class Response
{
    public Response() { }

    /**
     * Converts the response object to a well-formatted JSON string using Gson.
     * The output format of the JSON string is entirely dependent on the
     * specific implementation of the `SerializeContent` method in the
     * derived class.
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
     * Abstract method that subclasses must implement to serialize their
     * specific content into the JSON object. The output format of the
     * serialized JSON object is entirely determined by this method.
     *
     * @param jsonWriter The JsonWriter object used for serialization.
     * @throws IOException If an error occurs during JSON serialization.
     */
    protected abstract void SerializeContent(JsonWriter jsonWriter) throws IOException;

    /**
     * Parses a JSON string representing a network response and returns a
     * corresponding Response object. This static method deserializes the JSON
     * string based on the key at the beginning of the JSON object. Currently,
     * it supports "response" and "orderID" keys, and delegates deserialization
     * to the `SimpleResponse.FromJson` and 'OrderResponse.FromJson' methods.
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

            temp = jsonReader.nextName();

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
