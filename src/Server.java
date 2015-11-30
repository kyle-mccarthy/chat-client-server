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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

public class Server {
    protected int port;
    protected ServerSocket listener;
    protected int clientCount;
    protected int clientIDCount;
    protected int maxClients;
    protected HashMap<String, String> credentials;
    protected HashSet<String> users;
    protected HashMap<String, PrintWriter> clients;

    /**
     * Server constructor, take in the port from the main application and set the version and reset client count.
     *
     * @param port - int - the port that the server will run on
     */
    public Server(int port) {
        this.port = port;
        this.clientCount = 0;
        this.clientIDCount = 0;
        this.maxClients = 3;
        this.users = new HashSet<>();
        this.clients = new HashMap<>();
        this.credentials = new HashMap<>();
        this.loadCredentials();
    }

    /**
     * Load the default credentials provided in the assignment...
     */
    public void loadCredentials() {
        this.credentials.put("Tom", "Tom11");
        this.credentials.put("David", "David22");
        this.credentials.put("Beth", "Beth33");
        this.credentials.put("John", "John44");
    }

    /**
     * Start the ServerSocket on the desired port, increment the clientCount and create the Thread to handle comms.
     *
     * @throws IOException
     */
    public void run() throws IOException {
        this.listener = new ServerSocket(this.port);
        try {
            // run the loop infinitely and process new clients attempting to join the channel
            while (true) {
                // @todo limit the max connections
                this.clientCount++;
                this.clientIDCount++;
                new Handler(this.listener.accept(), clientIDCount).start();
            }
        } finally {
            // called when the process exits (client disconnects), close the listener and then also decrement the counter
            // so other people have the ability to connect to the chat room
            // decrement the client counter and close the connection
            this.clientCount--;
            this.listener.close();
        }
    }

    public boolean login(String username, String password) {
        String pass = this.credentials.get(username);
        return (pass != null && pass.equals(password));
    }

    public boolean send(String username, String message) {
        // treat case where username is all
        // username not all send to user with username
        return false;
    }

    public boolean who() {
        return false;
    }

    public boolean logout() {
        return false;
    }

    /**
     * Try to start the server on the default port defined in the instructions.  If there is an issue try to inform the
     * user of the most likely issue and output the stack trace.
     *
     * @param args
     */
    public static void main(String[] args) {
        Server server = new Server(13888);
        try {
            System.out.println("My chat room server. Version Two.");
            server.run();
        } catch (IOException e) {
            System.err.println("Error: Could not bind to port.  This could be the result of the server process" +
                    " exiting abnormally when last run.  View the README file to find how to close the process " +
                    " that is blocking the port.  The stack trace follows: \n");
            e.printStackTrace();
        }
    }


    private class Handler extends Thread {
        protected Socket socket;
        protected int clientID;
        protected String username;
        protected boolean authenticated;
        protected BufferedReader input;
        protected PrintWriter output;

        /**
         * Thread to handle the requests from a client application to the server.  A handler is created from the lister
         * loop inside the Server's run method.  A handler is only responsible for the communication of one single
         * client and the server.
         *
         * @param socket - Socket from the server object
         * @param id     - int - ID of the client running on the handler
         */
        public Handler(Socket socket, int id) {
            this.socket = socket;
            this.clientID = id;
            this.authenticated = false;
        }

        /**
         * Controls the thread for a client, allows for client to input a string that is then sent to the server.
         */
        public void run() {
            try {
                // create the BufferedReader and PrintWriter for input and output respectively
                this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                this.output = new PrintWriter(this.socket.getOutputStream(), true);

                // when a client connects send the following instructions, the client will need to handle these
                // and print them to the CLI at start or all the messages will be delay
                String welcome = "My chat room client.  Version Two. Your client has connected to the server and has" +
                        " been assigned the temporary id  #" + this.clientID + ". Please execute a command or enter help" +
                        " for a list of commands.";
                this.output.println(welcome);

                while (true) {
                    // get the command that is sent to the server and then process it according to the assignment
                    String input = this.input.readLine();
                    String tokens[] = input.split("\\s+");

                    // @todo remove debugging
                    // @todo process the commands from the client to the server
                    // direct the user to the readme if they don't know what to do
                    if (input.startsWith("help")) {
                        this.output.println("Please view the readme for a list of commands.");

                    } else if (input.startsWith("login")) {
                        // see if the tokens in the command are even valid
                        if (tokens.length != 3) {
                            this.output.println("Error: invalid login syntax.");
                        } else {
                            // attempt to login a user with the credentials passed
                            boolean status = login(tokens[1], tokens[2]);

                            // user was successfully authenticated, set the username, the authenticated variable, and notify
                            // the client that the authentication attempt was successful
                            if (status) {
                                this.username = tokens[1];
                                this.authenticated = true;
                                this.output.println("login confirmed");
                                System.out.println(this.username + " login");
                            } else {
                                this.output.println("Error: invalid login credentials");
                            }
                        }

                    } else if (input.startsWith("send")) {
                        // see if the tokens in the command are even valid
                        if (tokens.length != 3) {
                            this.output.println("Error: invalid send syntax.");
                        } else {
                            // send a message to the user specified
                            this.output.println("@todo");
                        }

                    } else if (input.startsWith("who")) {
                        // see if the tokens in the command are even valid
                        if (tokens.length != 1) {
                            this.output.println("Error: invalid who syntax.");
                        } else {
                            // output a list of all the clients connected to the server
                            this.output.println("@todo");
                        }

                    } else if (input.startsWith("logout")) {
                        // see if the tokens in the command are even valid
                        if (tokens.length != 1) {
                            this.output.println("Error: invalid logout syntax.");
                        } else {
                            // logout the client from the server
                            // @todo make the client cleaner when logged out somehow
                            this.output.println("Logging out...");
                            break;
                        }

                    } else {
                        // catch invalid commands and direct the user to the readme
                        this.output.println("Command not found.  View a list of possible commands in the readme.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // try to close the connection between handler and server
                try {
                    this.socket.close();
                    System.out.println("Connection with client closed.");
                } catch (IOException e) {
                    System.out.println("Error: exception occurred when closing the socket.  Possibly already closed?");
                }
            }
        }
    }
}
