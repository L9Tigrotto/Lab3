
import DataStructures.Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Listener
{
    private boolean _isRunning = false;
    private final int _port;

    private boolean _isStopRequested = false;
    private final Thread _thread = new Thread(this::Listen);

    public Listener(int port) { _port = port; }

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
        System.out.println("[INFO] Listening on port " + _port);
        System.out.println("[INFO] Type 'exit' to close the server");

        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        try (ExecutorService threadPool = new ThreadPoolExecutor(0, numberOfThreads, 10, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
             ServerSocket serverSocket = new ServerSocket(_port))
        {
            // set the serverSocket.accept() call blocking for 10 seconds before throwing a timeout exception
            // and check if _isStopRequested has set to true.
            serverSocket.setSoTimeout(10 * 1000);

            while (!_isStopRequested)
            {
                try
                {
                    Socket socket = serverSocket.accept();
                    Connection connection = new Connection(socket);
                    ClientHandler client = new ClientHandler(connection);
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

        System.out.println("[INFO] Stopped listening on port " + _port);
    }
}
