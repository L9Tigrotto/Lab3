
package Helpers;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * This class provides utility functions for common tasks such as connecting to servers,
 * reading data from JSON, and manipulating dates and times.
 */
public class Utilities
{
    /**
     * Attempts to connect to a server with retry logic in case of initial failures.
     * This method establishes a TCP socket connection to the specified server and
     * IP address. It includes retry logic to handle temporary connection issues.
     *
     * @param ip The IP address of the server to connect to.
     * @param port The port number of the server.
     * @param tries The number of times to attempt connection before giving up.
     * @param timeout The time to wait (in milliseconds) between retries.
     * @return A connected Socket object if successful, otherwise throws an exception.
     * @throws InterruptedException If the thread is interrupted while sleeping between retries.
     * @throws SocketException If the connection fails after all retries.
     */
    public static Socket TryConnect(String ip, int port, int tries, int timeout) throws InterruptedException, SocketException
    {
        boolean connected = false;
        Socket socket = null;
        int tryCount = 0;

        while (!connected && tryCount < tries)
        {
            System.out.printf("[INFO] Trying to connect to %s:%d\n", ip, port);

            try
            {
                // attempt to create a socket connection and set connected flag to true if successful
                socket = new Socket(ip, port);
                connected = true;
            }
            catch (IOException e)
            {
                tryCount++;
                // if not the last retry, wait before retrying
                if (tryCount < tries)
                {
                    System.out.printf("[ERROR] Could not connect to %s:%d. Retrying in %d ms\n", ip, port, timeout);
                    Thread.sleep(timeout); // pause before next retry
                }
                // log final failure message if retries are exhausted
                else { System.out.printf("[ERROR] Failed to connect to server: %s\n", e.getMessage()); }
            }
        }

        // if socket is still null after retries, throw SocketException
        if (socket == null) { throw new SocketException(); }
        return socket;
    }

    /**
     * Reads a string value from a JSON reader and ensures that the property name matches the expected value.
     *
     * @param reader The JsonReader object used to parse the JSON data.
     * @param expectedPropertyName The expected property name to match.
     * @return The string value of the property.
     * @throws IOException If an error occurs during reading or if the property name does not match.
     */
    public static String ReadString(JsonReader reader, String expectedPropertyName) throws IOException
    {
        String temp = reader.nextName();

        // if the property name doesn't match, throw an exception
        if (!temp.equalsIgnoreCase(expectedPropertyName)) { throw new IOException("Supposed to read '" + expectedPropertyName + "' from JSON (got " + temp + ")"); }
        return reader.nextString();
    }

    /**
     * Reads an integer value from a JSON reader and ensures that the property name matches the expected value.
     *
     * @param reader The JsonReader object used to parse the JSON data.
     * @param expectedPropertyName The expected property name to match.
     * @return The integer value of the property.
     * @throws IOException If an error occurs during reading or if the property name does not match.
     */
    public static int ReadInt(JsonReader reader, String expectedPropertyName) throws IOException
    {
        String temp = reader.nextName();

        // if the property name doesn't match, throw an exception
        if (!temp.equalsIgnoreCase(expectedPropertyName)) { throw new IOException("Supposed to read '" + expectedPropertyName + "' from JSON (got " + temp + ")"); }
        return reader.nextInt();
    }

    /**
     * Reads a long value from a JSON reader and ensures that the property name matches the expected value.
     *
     * @param reader The JsonReader object used to parse the JSON data.
     * @param expectedPropertyName The expected property name to match.
     * @return The long value of the property.
     * @throws IOException If an error occurs during reading or if the property name does not match.
     */
    public static long ReadLong(JsonReader reader, String expectedPropertyName) throws IOException
    {
        String temp = reader.nextName();

        // if the property name doesn't match, throw an exception
        if (!temp.equalsIgnoreCase(expectedPropertyName)) { throw new IOException("Supposed to read '" + expectedPropertyName + "' from JSON (got " + temp + ")"); }
        return reader.nextLong();
    }

    /**
     * Converts a timestamp in milliseconds to the year component.
     *
     * @param milliseconds The timestamp in milliseconds.
     * @return The year of the timestamp.
     */
    public static int GetYearFromMilliseconds(long milliseconds)
    {
        Instant instant = Instant.ofEpochMilli(milliseconds);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("GMT"));
        return zonedDateTime.getYear();
    }

    /**
     * Converts a timestamp in milliseconds to the month component.
     *
     * @param milliseconds The timestamp in milliseconds.
     * @return The month (1-12) of the timestamp.
     */
    public static int GetMonthFromMilliseconds(long milliseconds)
    {
        Instant instant = Instant.ofEpochMilli(milliseconds);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("GMT"));
        return zonedDateTime.getMonth().getValue();
    }

    /**
     * Converts a timestamp in milliseconds to the day of the month component.
     *
     * @param milliseconds The timestamp in milliseconds.
     * @return The day of the month (1-31) of the timestamp.
     */
    public static int GetDayOfMonthFromMilliseconds(long milliseconds)
    {
        Instant instant = Instant.ofEpochMilli(milliseconds);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("GMT"));
        return zonedDateTime.getDayOfMonth();
    }

    /**
     * Converts a timestamp in milliseconds to a formatted date string.
     *
     * @param milliseconds The timestamp in milliseconds.
     * @param format The format to convert the timestamp to.
     * @return A string representation of the timestamp in the specified format.
     */
    public static String MillisecondsToString(long milliseconds, String format)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return simpleDateFormat.format(milliseconds);
    }

    /**
     * Converts a date string to a timestamp in milliseconds based on a given format.
     *
     * @param dateString The date string to convert.
     * @param format The format of the date string.
     * @return The corresponding timestamp in milliseconds.
     * @throws ParseException If the date string does not match the given format.
     */
    public static long MillisecondsFromString(String dateString, String format) throws ParseException
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = simpleDateFormat.parse(dateString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // set the day of the month to 15th to avoid timezone errors (used to convert MM/YYYY to ms)
        calendar.set(Calendar.DAY_OF_MONTH, 15);
        return calendar.getTimeInMillis();
    }
}
