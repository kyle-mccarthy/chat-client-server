import java.io.IOException;
import java.net.ServerSocket;

/**
 * Student: Kyle McCarthy
 * Student ID: 1807388
 * Student Pawprint: KJMD54
 * Date: 11/23/15
 */

public class Server
{
    protected int port;
    protected ServerSocket listener;
    protected int clientCount;
    protected int version;

    public Server(int port)
    {
        this.port = port;
        this.clientCount = 0;
        this.version = 2;
    }

    protected String getServerHeader()
    {
        return "My chat room.  Version " + this.version;
    }

    public void run() throws IOException
    {

    }

    private static class Handler extends Thread
    {

    }
}
