/**
 * Student: Kyle McCarthy
 * Student ID: 1807388
 * Student Pawprint: KJMD54
 * Date: 11/23/15
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
    protected Socket socket;
    protected int port;
    protected String name;
    protected String address;
    protected BufferedReader input;
    protected PrintWriter output;

    /**
     * Create the client and set the default server address/IP to the localhost, allow for the port to be passed
     * to the client through the constructor.
     * @param port - int - port of the socket
     */
    public Client(int port)
    {
        this.port = port;
        this.address = "127.0.0.1";
    }

    public void connect() throws IOException
    {
        this.socket = new Socket(this.address, this.port);
        this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.output = new PrintWriter(this.socket.getOutputStream(), true);

        while (true) {
            // wait for the command
            System.out.print("> ");

            // read in the command from the CLI using scanner
            Scanner s = new Scanner(System.in);
            String command = s.next();

            // send the command to the server
            this.output.println(command);

            // get the response
            try {
                String response = this.input.readLine();
                System.out.println(response);
            } catch(IOException e) {
                e.getStackTrace();
            }

        }
    }

    public static void main (String[] args)
    {
        Client client = new Client(17388);
        try {
            client.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
