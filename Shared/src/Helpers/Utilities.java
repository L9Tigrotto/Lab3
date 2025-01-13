
package Helpers;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Calendar;

/**
 * This class provides utility functions.
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
                // if wasn't last retry
                if (tryCount < tries)
                {
                    System.out.printf("[ERROR] Could not connect to %s:%d. Retrying in %d ms\n", ip, port, timeout);
                    Thread.sleep(timeout); // pause before next retry
                }
                // log final failure message
                else { System.out.printf("[ERROR] Failed to connect to server: %s\n", e.getMessage()); }
            }
        }

        if (socket == null) { throw new SocketException(); }
        return socket;
    }

    public static String ReadString(JsonReader reader, String expectedPropertyName) throws IOException
    {
        String temp = reader.nextName();
        if (!temp.equalsIgnoreCase(expectedPropertyName)) { throw new IOException("Supposed to read '" + expectedPropertyName + "' from JSON (got " + temp + ")"); }
        return reader.nextString();
    }

    public static int ReadInt(JsonReader reader, String expectedPropertyName) throws IOException
    {
        String temp = reader.nextName();
        if (!temp.equalsIgnoreCase(expectedPropertyName)) { throw new IOException("Supposed to read '" + expectedPropertyName + "' from JSON (got " + temp + ")"); }
        return reader.nextInt();
    }

    public static long ReadLong(JsonReader reader, String expectedPropertyName) throws IOException
    {
        String temp = reader.nextName();
        if (!temp.equalsIgnoreCase(expectedPropertyName)) { throw new IOException("Supposed to read '" + expectedPropertyName + "' from JSON (got " + temp + ")"); }
        return reader.nextLong();
    }

    public static int GetYearFromMillis(long millis)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.get(Calendar.YEAR);
    }

    public static int GetMonthFromMillis(long millis)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.get(Calendar.MONTH);
    }

    public static int GetDayOfTheMonthFromMillis(long millis)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static long GetMillisFromYearAndMonth(int year, int month)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTimeInMillis();
    }
}
