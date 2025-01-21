
package Orders;

import Helpers.Tuple;
import Helpers.Utilities;
import Messages.GetPriceHistoryRequest;
import Messages.SimpleResponse;
import com.google.gson.FormattingStyle;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

// first layer list ordered by year and month
// second layer list ordered by day of month
// [
//      2024/09 : [
//          2024/09/01 : [
//              { price record 1 },
//              { price record 2 },
//              ...
//          ],
//          2024/09/02 : [
//              { price record 1 },
//              { price record 2 },
//              ...
//          ],
//          ...
//      ],
//      ...
// ]
/**
 * A singleton class that holds a collection of history records for trades,
 * organized by year, month, and day. This class supports adding records,
 * retrieving price history, and saving/loading the records to/from a file.
 */
public class HistoryRecordCollection
{
    // singleton instance of the HistoryRecordCollection
    private static final HistoryRecordCollection _instance = new HistoryRecordCollection();

    // collection storing records ordered by year, month, and day
    private final TreeSet<
            Tuple<Long, TreeSet< // years and months
                    Tuple<Long, List<HistoryRecord>> // days
                    >>
            > _collection;

    /**
     * Private constructor initializing the collection.
     * This constructor ensures that the records are sorted by year, month, and day.
     */
    private HistoryRecordCollection()
    {
        _collection = new TreeSet<>(
                (timestamp_days1, timestamp_days2) -> {
                    int year1 = Utilities.GetYearFromMilliseconds(timestamp_days1.GetX());
                    int month1 = Utilities.GetMonthFromMilliseconds(timestamp_days1.GetX());

                    int year2 = Utilities.GetYearFromMilliseconds(timestamp_days2.GetX());
                    int month2 = Utilities.GetMonthFromMilliseconds(timestamp_days2.GetX());

                    int comp = Integer.compare(year1, year2);
                    if (comp == 0) { comp = Integer.compare(month1, month2); }
                    return comp;
                });
    }

    /**
     * Creates a second layer of the collection, sorted by day of the month.
     *
     * @return A TreeSet representing the second layer (sorted by day).
     */
    private static TreeSet<Tuple<Long, List<HistoryRecord>>> CreateSecondLayer()
    {
        return new TreeSet<>(
                (timestamp_recodList1, timestamp_recodList2) ->
                {
                    int day1 = Utilities.GetDayOfMonthFromMilliseconds(timestamp_recodList1.GetX());
                    int day2 = Utilities.GetDayOfMonthFromMilliseconds(timestamp_recodList2.GetX());

                    return Integer.compare(day1, day2);
                });
    }

    /**
     * Retrieves the first layer (year and month) tuple for a given timestamp.
     *
     * @param timestamp The timestamp used to find the year and month.
     * @return A Tuple representing the first layer (year and month).
     */
    private Tuple<Long, TreeSet<Tuple<Long, List<HistoryRecord>>>> GetFirstLayerTuple(long timestamp)
    {
        Tuple<Long, TreeSet<Tuple<Long, List<HistoryRecord>>>> dummy = new Tuple<>(timestamp, null);
        Tuple<Long, TreeSet<Tuple<Long, List<HistoryRecord>>>> firstLayerTuple;

        if (_collection.contains(dummy)) { firstLayerTuple = _collection.ceiling(dummy); }
        else
        {
            TreeSet<Tuple<Long, List<HistoryRecord>>> secondLayer = CreateSecondLayer();
            firstLayerTuple = new Tuple<>(timestamp, secondLayer);
            _collection.add(firstLayerTuple);
        }

        return firstLayerTuple;
    }

    /**
     * Retrieves the second layer (day of month) tuple for a given first layer and timestamp.
     *
     * @param firstLayerTuple The first layer (year and month) tuple.
     * @param timestamp The timestamp used to find the day of the month.
     * @return A Tuple representing the second layer (day of month).
     */
    private Tuple<Long, List<HistoryRecord>> GetSecondLayerTuple(Tuple<Long, TreeSet<Tuple<Long, List<HistoryRecord>>>> firstLayerTuple, long timestamp)
    {
        Tuple<Long, List<HistoryRecord>> dummy = new Tuple<>(timestamp, null);
        TreeSet<Tuple<Long, List<HistoryRecord>>> secondLayer = firstLayerTuple.GetY();
        Tuple<Long, List<HistoryRecord>> secondLayerTuple;

        if (secondLayer.contains(dummy)) { secondLayerTuple = secondLayer.ceiling(dummy); }
        else
        {
            List<HistoryRecord> recordList = new ArrayList<>();
            secondLayerTuple = new Tuple<>(timestamp, recordList);
            secondLayer.add(secondLayerTuple);
        }

        return secondLayerTuple;
    }

    /**
     * Adds a history record to the internal collection, organizing it by year, month, and day.
     *
     * @param record The HistoryRecord to add.
     */
    private void AddInternal(HistoryRecord record)
    {
        synchronized (_collection)
        {
            Tuple<Long, TreeSet<Tuple<Long, List<HistoryRecord>>>> firstLayerTuple = GetFirstLayerTuple(record.GetTimestamp());
            Tuple<Long, List<HistoryRecord>> secondLayerTuple = GetSecondLayerTuple(firstLayerTuple, record.GetTimestamp());
            secondLayerTuple.GetY().add(record);
        }
    }

    /**
     * Retrieves the price history for a given request, returning a response.
     *
     * @param request The GetPriceHistoryRequest containing the timestamp.
     * @return A SimpleResponse containing the formatted price history.
     */
    private SimpleResponse GetPricesInternal(GetPriceHistoryRequest request)
    {
        String result;

        synchronized (_collection)
        {
            TreeSet<Tuple<Long, List<HistoryRecord>>> yearAndMonth = GetFirstLayerTuple(request.GetTimestamp()).GetY();

            try (StringWriter stringWriter = new StringWriter();
                 JsonWriter jsonWriter = new JsonWriter(stringWriter))
            {
                stringWriter.append("\n");

                jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
                jsonWriter.beginArray();

                for (Tuple<Long, List<HistoryRecord>> tuple : yearAndMonth)
                {
                    long open = 0, close = 0, max = 0, min = 0, count = 0;
                    for (HistoryRecord record : tuple.GetY())
                    {
                        if (count == 0) { open = record.GetPrice(); }
                        close = record.GetPrice();

                        if (min > close) { min = record.GetPrice(); }
                        if (max < close) { max = record.GetPrice(); }

                        count++;
                    }

                    if (count > 0)
                    {
                        jsonWriter.beginObject();

                        jsonWriter.name("day").value(Utilities.GetDayOfMonthFromMilliseconds(tuple.GetX()));
                        jsonWriter.name("open").value(open);
                        jsonWriter.name("close").value(close);
                        jsonWriter.name("min").value(min);
                        jsonWriter.name("max").value(max);

                        jsonWriter.endObject();
                    }
                }

                jsonWriter.endArray();
                result = stringWriter.toString();
            }
            catch (IOException e) { return GetPriceHistoryRequest.OTHER_ERROR_CASES; }
        }

        return new SimpleResponse(100, result);
    }

    /**
     * Loads history records from a file, parsing the data and organizing it by year, month, and day.
     *
     * @param filename The filename to load the history from.
     * @return The last used order ID from the loaded data.
     * @throws IOException If an error occurs while reading the file.
     */
    private long LoadInternal(String filename) throws IOException
    {
        File orderHistoryFile = new File(filename);
        if (!orderHistoryFile.exists()) { return 0; }

        long lastUsedID = 0;

        try (FileReader fileReader = new FileReader(orderHistoryFile);
             BufferedReader bufferedReader = new BufferedReader(fileReader);
             JsonReader jsonReader = new JsonReader(bufferedReader))
        {
            jsonReader.beginObject();

            String temp = jsonReader.nextName();
            if (!temp.equalsIgnoreCase("trades")) { throw new IOException("Supposed to read 'trades' from JSON (got " + temp + ")"); }
            jsonReader.beginArray();

            while (jsonReader.hasNext())
            {
                jsonReader.beginObject();

                long orderID = Utilities.ReadLong(jsonReader, "orderID");
                Method method = Method.FromString(Utilities.ReadString(jsonReader, "type"));
                Type type = Type.FromString(Utilities.ReadString(jsonReader, "orderType"));
                long size = Utilities.ReadLong(jsonReader, "size");
                long price = Utilities.ReadLong(jsonReader, "price");
                long timestamp = Utilities.ReadLong(jsonReader, "timestamp") * 1000;

                // check if te current month/year exists in the list
                HistoryRecord record = new HistoryRecord(orderID, method, type, size, price, timestamp);
                AddInternal(record);

                if (lastUsedID < orderID) { lastUsedID = orderID; }

                jsonReader.endObject();
            }

            jsonReader.endArray();
            jsonReader.endObject();
        }

        return lastUsedID;
    }

    /**
     * Saves the history records to a file in JSON format.
     *
     * @param filename The filename to save the history to.
     * @throws IOException If an error occurs while writing to the file.
     */
    private void SaveInternal(String filename) throws IOException
    {
        File orderHistoryFile = new File(filename);
        if (_collection.isEmpty()) { return; }

        synchronized (_collection)
        {
            try (FileWriter fileWriter = new FileWriter(orderHistoryFile);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                 JsonWriter jsonWriter = new JsonWriter(bufferedWriter))
            {
                jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
                jsonWriter.beginObject();

                jsonWriter.name("trades");
                jsonWriter.beginArray();

                for (Tuple<Long, TreeSet<Tuple<Long, List<HistoryRecord>>>> firstLayer : _collection)
                {
                    for (Tuple<Long, List<HistoryRecord>> secondLayer : firstLayer.GetY())
                    {
                        for (HistoryRecord record : secondLayer.GetY())
                        {
                            jsonWriter.beginObject();

                            jsonWriter.name("orderID").value(record.GetID());
                            jsonWriter.name("type").value(record.GetMethod().ToString());
                            jsonWriter.name("orderType").value(record.GetType().ToString());
                            jsonWriter.name("size").value(record.GetSize());
                            jsonWriter.name("price").value(record.GetPrice());
                            jsonWriter.name("timestamp").value(record.GetTimestamp() / 1000);

                            jsonWriter.endObject();
                        }
                    }
                }

                jsonWriter.endArray();
                jsonWriter.endObject();
            }
        }
    }

    // public static methods to access functionality

    public static void Add(HistoryRecord record) { _instance.AddInternal(record); }
    public static SimpleResponse GetPrices(GetPriceHistoryRequest request) { return _instance.GetPricesInternal(request); }
    public static long Load(String filename) throws IOException { return _instance.LoadInternal(filename); }
    public static void Save(String filename) throws IOException { _instance.SaveInternal(filename); }
}
