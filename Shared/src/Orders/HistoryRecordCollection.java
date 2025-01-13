
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
// second layer list ordered by day
// [
//      2021/01 : [
//          { price history 1 },
//          { price history 2 },
//          ...
//      ],
//      ...
//      2021/03 : [
//          { price history 1 },
//          { price history 2 },
//      ...
//      ],
//      ...
// ]
public class HistoryRecordCollection
{
    private static final HistoryRecordCollection _instance = new HistoryRecordCollection();

    private final TreeSet<Tuple<Long, List<Tuple<Long, HistoryRecord>>>> _collection;

    private HistoryRecordCollection()
    {
        _collection = new TreeSet<>(
                HistoryRecordCollection::compare);
    }

    private static int compare(Tuple<Long, List<Tuple<Long, HistoryRecord>>> tuple1, Tuple<Long, List<Tuple<Long, HistoryRecord>>> tuple2)
    {
        int year1 = Utilities.GetYearFromMilliseconds(tuple1.GetX());
        int month1 = Utilities.GetMonthFromMilliseconds(tuple1.GetX());

        int year2 = Utilities.GetYearFromMilliseconds(tuple2.GetX());
        int month2 = Utilities.GetMonthFromMilliseconds(tuple2.GetX());

        int comp = Integer.compare(year1, year2);
        if (comp == 0)
        {
            comp = Integer.compare(month1, month2);
        }
        return comp;
    }

    private void AddInternal(HistoryRecord record)
    {
        Tuple<Long, List<Tuple<Long, HistoryRecord>>> dummy = new Tuple<>(record.GetTimestamp(), null);
        synchronized (_collection)
        {
            Tuple<Long, List<Tuple<Long, HistoryRecord>>> firstLayerTuple = _collection.ceiling(dummy);
            List<Tuple<Long, HistoryRecord>> secondLayerTuple;

            if (firstLayerTuple == null)
            {
                secondLayerTuple = new ArrayList<>();
                firstLayerTuple = new Tuple<>(record.GetTimestamp(), secondLayerTuple);
                _collection.add(firstLayerTuple);
            }
            else { secondLayerTuple = firstLayerTuple.GetY(); }

            secondLayerTuple.add(new Tuple<>(record.GetTimestamp(), record));
        }
    }

    private List<Tuple<Long, HistoryRecord>> GetSpecificMonth(long timestamp)
    {
        Tuple<Long, List<Tuple<Long, HistoryRecord>>> dummy = new Tuple<>(timestamp, null);
        Tuple<Long, List<Tuple<Long, HistoryRecord>>> firstLayerTuple = _collection.ceiling(dummy);

        if (firstLayerTuple == null) { return new ArrayList<>(); }
        return firstLayerTuple.GetY();
    }

    private SimpleResponse GetPricesInternal(GetPriceHistoryRequest request)
    {
        String result;
        int currentDay = -1;
        long open = 0, close = 0, max = 0, min = 0;
        int counter = 0;

        synchronized (_collection)
        {
            List<Tuple<Long, HistoryRecord>> tuples = GetSpecificMonth(request.GetTimestamp());
            tuples.sort((tuple1, tuple2) ->
            {
                int day1 = Utilities.GetDayOfMonthFromMilliseconds(tuple1.GetX());
                int day2 = Utilities.GetDayOfMonthFromMilliseconds(tuple2.GetX());

                int comp = Integer.compare(day1, day2);
                if (comp == 0) { comp = Long.compare(tuple1.GetY().GetID(), tuple2.GetY().GetID()); }
                return comp;
            });

            try (StringWriter stringWriter = new StringWriter();
                 JsonWriter jsonWriter = new JsonWriter(stringWriter))
            {
                stringWriter.append("\n");

                jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
                jsonWriter.beginArray();

                for (Tuple<Long, HistoryRecord> tuple : tuples)
                {
                    HistoryRecord record = tuple.GetY();
                    int day = Utilities.GetDayOfMonthFromMilliseconds(tuple.GetX());

                    if (day != currentDay)
                    {
                        if (currentDay != -1)
                        {
                            jsonWriter.beginObject();

                            System.out.printf("Counter: %d\n", ++counter);

                            jsonWriter.name("day").value(day);
                            jsonWriter.name("open").value(open);
                            jsonWriter.name("close").value(close);
                            jsonWriter.name("min").value(min);
                            jsonWriter.name("max").value(max);

                            jsonWriter.endObject();
                        }

                        currentDay = day;
                        open = close = min = max = record.GetPrice();
                    }
                    else
                    {
                        close = record.GetPrice();
                        if (min > close) { min = close; }
                        if (max < close) { max = close; }
                    }
                }

                jsonWriter.endArray();
                result = stringWriter.toString();
            }
            catch (IOException e) { return GetPriceHistoryRequest.OTHER_ERROR_CASES; }
        }

        return new SimpleResponse(100, result);
    }

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

    private void SaveInternal(String filename) throws IOException
    {
        File orderHistoryFile = new File(filename);
        if (!orderHistoryFile.exists()) { return; }

        try (FileWriter fileWriter = new FileWriter(orderHistoryFile);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             JsonWriter jsonWriter = new JsonWriter(bufferedWriter))
        {
            jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
            jsonWriter.beginObject();

            jsonWriter.name("trades");
            jsonWriter.beginArray();

            for (Tuple<Long, List<Tuple<Long, HistoryRecord>>> firstLevel : _collection)
            {
                for (Tuple<Long, HistoryRecord> secondLayer : firstLevel.GetY())
                {
                    jsonWriter.beginObject();

                    HistoryRecord record = secondLayer.GetY();
                    jsonWriter.name("orderID").value(record.GetID());
                    jsonWriter.name("type").value(record.GetMethod().ToString());
                    jsonWriter.name("orderType").value(record.GetType().ToString());
                    jsonWriter.name("size").value(record.GetSize());
                    jsonWriter.name("price").value(record.GetPrice());
                    jsonWriter.name("timestamp").value(record.GetTimestamp() / 1000);

                    jsonWriter.endObject();
                }
            }

            jsonWriter.endArray();
            jsonWriter.endObject();
        }
    }

    public static void Add(HistoryRecord record) { _instance.AddInternal(record); }
    public static SimpleResponse GetPrices(GetPriceHistoryRequest request) { return _instance.GetPricesInternal(request); }
    public static long Load(String filename) throws IOException { return _instance.LoadInternal(filename); }
    public static void Save(String filename) throws IOException { _instance.SaveInternal(filename); }
}
