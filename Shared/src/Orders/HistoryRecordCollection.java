package Orders;

import Helpers.Tuple;
import Helpers.Utilities;
import com.google.gson.FormattingStyle;
import com.google.gson.JsonIOException;
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
                (tuple1, tuple2) ->
                {
                    int year1 = Utilities.GetYearFromMillis(tuple1.GetX());
                    int month1 = Utilities.GetMonthFromMillis(tuple1.GetX());

                    int year2 = Utilities.GetYearFromMillis(tuple2.GetX());
                    int month2 = Utilities.GetMonthFromMillis(tuple2.GetX());

                    int comp = Integer.compare(year1, year2);
                    if (comp == 0) { comp = Integer.compare(month1, month2); }
                    return comp;
                });
    }

    private void AddInternal(HistoryRecord record)
    {
        Tuple<Long, List<Tuple<Long, HistoryRecord>>> dummy = new Tuple<>(record.GetTimestamp(), null);
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
                    jsonWriter.name("timestamp").value(record.GetTimestamp());

                    jsonWriter.endObject();
                }
            }

            jsonWriter.endArray();
            jsonWriter.endObject();
        }
    }

    public static void Add(HistoryRecord record) { _instance.AddInternal(record); }
    public static long Load(String filename) throws IOException { return _instance.LoadInternal(filename); }
    public static void Save(String filename) throws IOException { _instance.SaveInternal(filename); }
}
