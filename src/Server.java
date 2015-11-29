import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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

    /**
     * Server constructor, take in the port from the main application and set the version and reset client count.
     * @param port - int - the port that the server will run on
     */
    public Server(int port)
    {
        this.port = port;
        this.clientCount = 0;
        this.version = 2;
    }

    /**
     * Getter for the chat room header per the instructions for grading purposes.
     * @return - string - chat room version
     */
    protected String getServerHeader()
    {
        return "My chat room.  Version " + this.version;
    }

    public void run() throws IOException
    {
        this.listener = new ServerSocket(this.port);

    }

    private static class Handler extends Thread
    {
        protected Socket socket;
        protected int clientID;

        /**
         *
         * @param socket - Socket from the server object
         * @param id - int - ID of the client running on the handler
         */
        public Handler(Socket socket, int id)
        {
            this.socket = socket;
            this.clientID = id;
        }
    }
}
