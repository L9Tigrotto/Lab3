
package Messages;

import Helpers.Utilities;
import Networking.OperationType;
import Networking.Request;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.ParseException;

/**
 * This class represents a request to get the price history for a specific month and year.
 * It extends the `Request` class and provides methods to serialize and deserialize
 * the request data to and from JSON format.
 */
public class GetPriceHistoryRequest extends Request
{
    // constant to define the date format for the request (Month and Year)
    public static final String DATE_FORMAT = "MMyyyy";

    // Predefined simple responses for error handling
    public static final SimpleResponse USER_NOT_LOGGED = new SimpleResponse(101, "user not logged in");
    public static final SimpleResponse OTHER_ERROR_CASES = new SimpleResponse(102, "other_error_cases");

    // the timestamp representing the date for which price history is requested
    private final long _timestamp;

    /**
     * Constructor that initializes the request with a timestamp.
     *
     * @param timestamp The timestamp (in milliseconds) representing the desired month and year.
     */
    public GetPriceHistoryRequest(long timestamp)
    {
        super(OperationType.GET_PRICE_HISTORY);
        _timestamp = timestamp;
    }

    /**
     * Getter for the timestamp.
     *
     * @return The timestamp (in milliseconds) of the request.
     */
    public long GetTimestamp() { return _timestamp; }

    /**
     * Getter for the month extracted from the timestamp.
     *
     * @return The month part of the timestamp.
     */
    public int GetMonth() { return Utilities.GetMonthFromMilliseconds(_timestamp); }

    /**
     * Getter for the year extracted from the timestamp.
     *
     * @return The year part of the timestamp.
     */
    public int GetYear() { return Utilities.GetYearFromMilliseconds(_timestamp); }

    /**
     * Serializes the content of this GetPriceHistoryRequest to a JSON writer.
     * The timestamp is converted to a string in the defined date format (MMyyyy).
     *
     * @param jsonWriter The JSON writer to serialize the content to.
     * @throws IOException If an I/O error occurs while writing.
     */
    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        String formattedTimestamp = Utilities.MillisecondsToString(_timestamp, DATE_FORMAT);
        jsonWriter.name("month").value(formattedTimestamp);
    }

    /**
     * Deserializes a GetPriceHistoryRequest from a JSON reader.
     * The method expects the "month" field in the JSON, which is in the "MMyyyy" format.
     *
     * @param jsonReader The JSON reader to read the request content from.
     * @return A new GetPriceHistoryRequest instance with the deserialized timestamp.
     * @throws IOException If an I/O error occurs while reading.
     * @throws ParseException If the timestamp string cannot be parsed into a valid timestamp.
     */
    public static GetPriceHistoryRequest DeserializeContent(JsonReader jsonReader) throws IOException, ParseException
    {
        String formattedTimestamp = Utilities.ReadString(jsonReader, "month");
        long timestamp = Utilities.MillisecondsFromString(formattedTimestamp, DATE_FORMAT);

        return new GetPriceHistoryRequest(timestamp);
    }

}
