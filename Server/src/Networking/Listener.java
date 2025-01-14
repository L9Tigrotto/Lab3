
package Networking;

import Helpers.GlobalData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Listener
{
    private final AtomicBoolean _isRunning;
    private final AtomicBoolean _isStopRequested;

    private final Thread _thread = new Thread(this::Listen);

    public Listener()
    {
        _isRunning = new AtomicBoolean(false);
        _isStopRequested = new AtomicBoolean(false);
    }

    public boolean IsRunning() { return _isRunning.get(); }
    public boolean IsStopRequested() { return _isStopRequested.get(); }

    public void Start()
    {
        if (_isRunning.compareAndExchange(false, true))
        {
            System.out.printf("[WARNING] Already listening on port %d\n", GlobalData.SETTINGS.TCP_PORT);
            return;
        }

        _thread.start();
    }

    public void Stop()
    {
        if (!_isRunning.compareAndExchange(true, false))
        {
            System.out.printf("[WARNING] Not listening on port %d\n", GlobalData.SETTINGS.TCP_PORT);
            return;
        }

        System.out.println("[INFO] Waiting for listener to close...");
        _isStopRequested.set(true);
        try { _thread.join(); }
        catch (InterruptedException e) { System.out.printf("[ERROR] Unable to join the listener thread: %s\n", e.getMessage()); }
    }

    private void Listen()
    {
        System.out.printf("[INFO] Listening on port %d\n", GlobalData.SETTINGS.TCP_PORT);

        BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(GlobalData.SETTINGS.MaxConcurrentClients);
        try (ExecutorService threadPool = new ThreadPoolExecutor(0, GlobalData.SETTINGS.MaxConcurrentClients,
                1, TimeUnit.SECONDS, taskQueue);
             ServerSocket serverSocket = new ServerSocket(GlobalData.SETTINGS.TCP_PORT))
        {
            // set the serverSocket.accept() call blocking for AcceptTimeoutMS ms before throwing a timeout exception
            // and check if _isStopRequested has set to true.
            serverSocket.setSoTimeout(GlobalData.SETTINGS.AcceptClientTimeoutMS);

            while (!_isStopRequested.compareAndSet(true, false))
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
            try { boolean closed = threadPool.awaitTermination(GlobalData.SETTINGS.ClientInactiveThresholdMS + 2, TimeUnit.MINUTES); }
            catch (InterruptedException e) { System.out.printf("[ERROR] Unable to terminate thread pool correctly: %s\n", e.getMessage()); }
        } catch (IOException e) { System.out.printf("[ERROR] I/O exception: %s\n", e.getMessage()); }
    }
}
