import java.io.IOException;

/**
 * Student: Kyle McCarthy
 * Student ID: 1807388
 * Student Pawprint: KJMD54
 * Date: 11/23/15
 */

public class Main
{
    public static void main(String[] args)
    {
        // the default port to run the server on
        int port = 17388;

        // try to start the server
        Server server = new Server(port);
        try {
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // try to connect a client
        Client client = new Client(port);
        try {
            client.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
