import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
        this.getServerHeader();
        this.listener = new ServerSocket(this.port);
        try {
            while (true) {
                new Handler(this.listener.accept(), clientCount++).start();
            }
        } finally {
            this.listener.close();
        }
    }

    public static void main (String[] args)
    {
        Server server = new Server(17388);
        try {
            server.run();
        } catch (IOException e) {
            System.err.println("Error: Could not bind to port.  This could be the result of the server process" +
                    " exiting abnormally when last run.  View the README file to find how to close the process " +
                    " that is blocking the port.  The stack trace follows: \n");
            e.printStackTrace();
        }
    }

    private static class Handler extends Thread
    {
        protected Socket socket;
        protected int clientID;
        protected BufferedReader input;
        protected PrintWriter output;

        /**
         * Thread to handle the requests from a client application to the server.
         * @param socket - Socket from the server object
         * @param id - int - ID of the client running on the handler
         */
        public Handler(Socket socket, int id)
        {
            this.socket = socket;
            this.clientID = id;
        }

        /**
         * Controls the thread for a client, allows for client to input a string that is then sent to the server.
         */
        public void run()
        {
            try {
                // create the BufferedReader and PrintWriter for input and output respectively
                this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                this.output = new PrintWriter(this.socket.getOutputStream(), true);

                while (true) {
                    // get the command that is sent to the server and then process it according to the assignment
                    String input = this.input.readLine();
                    System.out.println(input);

                    // @todo remove debugging
                    // @todo process the commands from the client to the server
                    this.output.println("OK");
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // try to close the connection between handler and server
                try {
                    this.socket.close();
                    System.out.print("Connection with client closed.");
                } catch (IOException e) {
                    this.output.println("Error: could not close the socket.");
                }
            }
        }
    }
}
