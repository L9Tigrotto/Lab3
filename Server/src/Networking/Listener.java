
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
    // atomic flags to manage listener state and stop request
    private final AtomicBoolean _isRunning;
    private final AtomicBoolean _isStopRequested;

    // a dedicated thread to handle the listening process
    private final Thread _thread = new Thread(this::Listen);

    public Listener()
    {
        _isRunning = new AtomicBoolean(false);
        _isStopRequested = new AtomicBoolean(false);
    }

    // returns whether the listener is currently running
    public boolean IsRunning() { return _isRunning.get(); }

    // returns whether a stop request has been made
    public boolean IsStopRequested() { return _isStopRequested.get(); }

    /**
     * Starts the listener thread. If the listener is already running,
     * it outputs a warning message and does not start another listener.
     */
    public void Start()
    {
        // ensures that the listener is only started once
        if (_isRunning.compareAndExchange(false, true))
        {
            System.out.printf("[WARNING] Already listening on port %d\n", GlobalData.SETTINGS.TCP_PORT);
            return;
        }

        _thread.start();
    }

    /**
     * Stops the listener by setting the stop request flag and waiting for the
     * listener thread to shut down. If the listener is not running, it outputs
     * a warning message.
     */
    public void Stop()
    {
        // ensures that the listener can only be stopped if it is running
        if (!_isRunning.compareAndExchange(true, false))
        {
            System.out.printf("[WARNING] Not listening on port %d\n", GlobalData.SETTINGS.TCP_PORT);
            return;
        }

        System.out.println("[INFO] Waiting for listener to close...");
        _isStopRequested.set(true);

        // wait for the listener thread to finish
        try { _thread.join(); }
        catch (InterruptedException e) { System.out.printf("[ERROR] Unable to join the listener thread: %s\n", e.getMessage()); }
    }

    /**
     * The method where the listener waits for client connections and handles
     * client requests. It accepts incoming client connections and delegates
     * handling to a thread pool.
     */
    private void Listen()
    {
        System.out.printf("[INFO] Listening on port %d\n", GlobalData.SETTINGS.TCP_PORT);

        BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(GlobalData.SETTINGS.MaxConcurrentClients);
        try (ExecutorService threadPool = new ThreadPoolExecutor(0, GlobalData.SETTINGS.MaxConcurrentClients,
                1, TimeUnit.SECONDS, taskQueue);
             ServerSocket serverSocket = new ServerSocket(GlobalData.SETTINGS.TCP_PORT))
        {
            // set a timeout for the accept call to avoid blocking indefinitely
            serverSocket.setSoTimeout(GlobalData.SETTINGS.AcceptClientTimeoutMS);

            // listen for incoming connections until the stop request is triggered
            while (!_isStopRequested.compareAndSet(true, false))
            {
                try
                {
                    Socket socket = serverSocket.accept();

                    // create a ClientHandler to manage the new client
                    ClientHandler client = new ClientHandler(socket);
                    threadPool.submit(client);
                }
                catch (SocketTimeoutException e) { continue; }
            }

            threadPool.shutdown();

            // wait for all tasks in the thread pool to complete before fully shutting down
            try { boolean closed = threadPool.awaitTermination(GlobalData.SETTINGS.ClientInactiveThresholdMS + 2, TimeUnit.MINUTES); }
            catch (InterruptedException e) { System.out.printf("[ERROR] Unable to terminate thread pool correctly: %s\n", e.getMessage()); }
        } catch (IOException e) { System.out.printf("[ERROR] I/O exception: %s\n", e.getMessage()); }
    }
}
