/**
 * Student: Kyle McCarthy
 * Student ID: 18073888
 * Student Pawprint: KJMD54
 * Date: 11/23/15
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    protected Socket socket;
    protected int port;
    protected String address;
    protected BufferedReader input;
    protected PrintWriter output;


    /**
     * Create the client and set the default server address/IP to the localhost, allow for the port to be passed
     * to the client through the constructor.
     *
     * @param port - int - port of the socket
     */
    public Client(int port) {
        this.port = port;
        this.address = "127.0.0.1";
    }

    /**
     * Connect the client to the server, and process the initial commands.  The client will then continue with the
     * infinite loop that process the client commands and forwards them to the server, the loop also processes data
     * that has been sent by the server and writes it to the CLI.
     *
     * @throws IOException
     */
    public void connect() throws IOException {
        this.socket = new Socket(this.address, this.port);
        this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

        System.out.println(this.input.readLine());

        // start the system in handler to get client commands
        new Handler(this.socket).start();

        // infinite loop for processing input and output to-from the client-server application
        while (true) {
            try {
                // get the response
                String response = this.input.readLine();
                // print the response from the server
                if (response != null && response.length() > 0) {
                    System.out.println(response);
                    System.out.print("> ");
                }
            } catch (IOException e) {
                e.getStackTrace();
            }

        }
    }

    /**
     * Run the client CLI, and use the default port defined by 1 : last 4 of student ID.
     */
    public static void main(String[] args) {
        Client client = new Client(13888);
        try {
            client.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hanlder thread to allow for the client to get keyboard input while listening to the server and print responses
     * from the server
     */
    public class Handler extends Thread {
        protected Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                output = new PrintWriter(this.socket.getOutputStream(), true);

                // infinite loop for getting user input
                while (true) {
                    // wait for the command
                    System.out.print("> ");

                    // read in the command from the CLI using scanner
                    Scanner s = new Scanner(System.in);
                    String command = s.nextLine();

                    // send the command to the server
                    output.println(command);

                    // @todo find a cleaner solution than checking to see if we sent the logout command
                    if (command.equals("logout")) {
                        break;
                    }
                }
            } catch (IOException e) {
                e.getStackTrace();
            }
        }
    }
}
