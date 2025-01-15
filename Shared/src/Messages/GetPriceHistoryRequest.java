
package Messages;

import Helpers.Utilities;
import Networking.OperationType;
import Networking.Request;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.ParseException;

public class GetPriceHistoryRequest extends Request
{
    public static final String DATE_FORMAT = "MMyyyy";

    public static final SimpleResponse USER_NOT_LOGGED = new SimpleResponse(101, "user not logged in");
    public static final SimpleResponse OTHER_ERROR_CASES = new SimpleResponse(102, "other_error_cases");

    private final long _timestamp;

    public GetPriceHistoryRequest(long timestamp)
    {
        super(OperationType.GET_PRICE_HISTORY);
        _timestamp = timestamp;
    }

    public long GetTimestamp() { return _timestamp; }
    public int GetMonth() { return Utilities.GetMonthFromMilliseconds(_timestamp); }
    public int GetYear() { return Utilities.GetYearFromMilliseconds(_timestamp); }

    protected void SerializeContent(JsonWriter jsonWriter) throws IOException
    {
        String formattedTimestamp = Utilities.MillisecondsToString(_timestamp, DATE_FORMAT);
        jsonWriter.name("month").value(formattedTimestamp);
    }

    public static GetPriceHistoryRequest DeserializeContent(JsonReader jsonReader) throws IOException, ParseException
    {
        String formattedTimestamp = Utilities.ReadString(jsonReader, "month");
        long timestamp = Utilities.MillisecondsFromString(formattedTimestamp, DATE_FORMAT);

        return new GetPriceHistoryRequest(timestamp);
    }

}
