import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.*;

public class Listener
{
    private boolean _isRunning;
    private volatile boolean _isStopRequested; // made volatile to avoid thread to cache this value (always updated)

    private final Thread _thread = new Thread(this::Listen);

    public Listener()
    {
        _isRunning = false;
        _isStopRequested = false;
    }

    public boolean IsRunning() { return _isRunning; }
    public boolean IsStopRequested() { return _isStopRequested; }

    public synchronized void Start()
    {
        if (_isRunning)
        {
            System.out.println("[Warning] Server is already running");
            return;
        }

        _thread.start();
        _isRunning = true;
    }

    public synchronized void Stop()
    {
        if (!_isRunning)
        {
            System.out.println("[Warning] Server is not running.");
            return;
        }
        _isStopRequested = true;

        System.out.println("[INFO] Waiting for server to stop...");
        try { _thread.join(); }
        catch (InterruptedException e) { throw new RuntimeException(e); }

        _isRunning = false;
        _isStopRequested = false;
    }

    private void Listen()
    {
        System.out.printf("[INFO] Listening on port %d. Type 'stop' to close the server\n", Main.SETTINGS.TCPPort);

        BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(Main.SETTINGS.MaxHandledClients);
        try (ExecutorService threadPool = new ThreadPoolExecutor(0, Main.SETTINGS.MaxHandledClients, 1, TimeUnit.SECONDS, taskQueue);
             ServerSocket serverSocket = new ServerSocket(Main.SETTINGS.TCPPort))
        {
            // set the serverSocket.accept() call blocking for 10 seconds before throwing a timeout exception
            // and check if _isStopRequested has set to true.
            serverSocket.setSoTimeout(10 * 1000);

            while (!_isStopRequested)
            {
                try
                {
                    Socket socket = serverSocket.accept();
                    ClientHandler client = new ClientHandler(socket);
                    threadPool.submit(client);
                }
                catch (SocketTimeoutException e) { continue; }
            }

            threadPool.shutdown();
            try { boolean closed = threadPool.awaitTermination(1, TimeUnit.MINUTES); }
            catch (InterruptedException e) { throw new RuntimeException(e); }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.printf("[INFO] Stopped listening on port %d.\n", Main.SETTINGS.TCPPort);
    }
}
