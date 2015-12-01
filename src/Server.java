/**
 * Student: Kyle McCarthy
 * Student ID: 18073888
 * Student Pawprint: KJMD54
 * Date: 11/23/15
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

public class Server {
    protected int port;
    protected ServerSocket listener;
    protected int clientCount;
    protected int clientIDCount;
    protected int maxClients;
    protected HashMap<String, String> credentials;
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
        this.clients = new HashMap<>();
        this.credentials = new HashMap<>();
        this.loadCredentials();
    }

    /**
     * Load the DB credentials from the file called database.  Nothing special just text file with users listed line
     * by line in format username:password.  Important to note that the passwords are stored in plain text!!!
     */
    public void loadCredentials() {
        // load the credentials from the database
        try {
            BufferedReader db = new BufferedReader(new FileReader("database.txt"));
            String line;
            // load the credentials line by line, tokenize at : which is username:password
            while ((line = db.readLine()) != null) {
                String tokens[] = line.split(":");
                if (tokens.length >= 2) {
                    this.credentials.put(tokens[0], tokens[1]);
                }
            }
            db.close();
            System.out.println("System: credentials loaded from database");
        } catch (IOException e) {
            System.out.println("Error: could not load user credentials due to exception.");
        }
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

    /**
     * Try to log the user in with the passed credentials.  If the attempt is successful, broadcast a message from the
     * system to all of the users in the chat room and notify them that the particular user has joined.
     *
     * @param username - HashMap key - users
     * @param password - HashMap value - users
     * @return bool - login attempt outcome
     */
    public boolean login(String username, String password) {
        String pass = this.credentials.get(username);
        if (pass != null && pass.equals(password)) {
            send("System", "all", username + " has joined the room.");
            return true;
        }
        return false;
    }

    /**
     * Send a message from user to username with contents message.  The user can be sending to another specific user, or
     * can be sending the message to everyone in the chat.
     *
     * @param user     - String - sender
     * @param username - String - receiver
     * @param message  - String - the message
     * @return bool - send attempt status
     */
    public boolean send(String user, String username, String message) {
        // treat case where username is all
        if (username.equals("all")) {
            // send to all the clients
            System.out.println(user + " to all " + message);
            for (PrintWriter clientStream : this.clients.values()) {
                clientStream.println(user + ": " + message);
            }
            return true;
        }
        // username not all send to user with username
        PrintWriter userStream = this.clients.get(username);
        if (userStream != null) {
            System.out.println(user + " to " + username + " " + message);
            userStream.println(user + ": @" + username + " " + message);
            return true;
        }
        return false;
    }

    /**
     * Try to logout a user and then if successful broadcast a message to the room notifying them that the user left.
     *
     * @param username - HashMap key - users
     * @return - logout attempt status
     */
    public boolean logout(String username) {
        if (clients.remove(username) != null) {
            send("System", "all", username + " left the room.");
            return true;
        }
        return false;
    }

    /**
     * Attempt to register a user to the client-server chat application.  This includes storing the user info in the database
     * to ensure it is persistent and it also includes adding it to the HashMap that has a copy of the credentials in memory.
     * @param username - new user username
     * @param password - new user password
     * @return - register attempt status
     */
    public boolean register(String username, String password)
    {
        // make sure that the username is unique
        if (!this.credentials.containsKey(username)) {
            // register the new user to the database
            this.credentials.put(username, password);
            // create the file writer
            PrintWriter db;
            try {
                // try to write the user info to a database file
                db = new PrintWriter(new BufferedWriter(new FileWriter("database.txt", true)));
                db.println(username + ":" + password);
                db.close();
                System.out.println("System:" + username + " registered an account.");
                return true;
            } catch (IOException e) {
                System.out.println("Error writing to database!");
            }
        }
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

                    // make sure that the input isn't null - this catches a bug when the client disconnects using ^C
                    if (input == null) {
                        break;
                    }

                    // tokenize the string - needed for checking the command
                    String tokens[] = input.split("\\s+");

                    // direct the user to the readme if they don't know what to do
                    if (input.startsWith("help")) {
                        this.output.println("Please view the readme for a list of commands.");

                    } else if (input.startsWith("login")) {
                        // see if the tokens in the command are even valid
                        if (tokens.length != 3) {
                            this.output.println("Error: invalid login syntax.");
                        } else if (clients.containsKey(tokens[1])) {
                            // see if the user is already logged in on a different client
                            this.output.println("Error: this user is already logged in.  Please quit the session in" +
                                    " the other client to initiate a connection for this user on this client.");
                        } else if (this.authenticated) {
                            // see if the user is trying to login twice for whatever reason
                            this.output.println("Error: you are already logged in.  Please log the user out of the current " +
                                    "client to login as a different user.");
                        } else {
                            // attempt to login a user with the credentials passed
                            boolean status = login(tokens[1], tokens[2]);

                            // user was successfully authenticated, set the username, the authenticated variable, and notify
                            // the client that the authentication attempt was successful
                            if (status) {
                                this.username = tokens[1];
                                this.authenticated = true;
                                this.output.println("login confirmed");
                                // add the users writer to the clients list
                                clients.put(this.username, this.output);
                            } else {
                                this.output.println("Error: invalid login credentials");
                            }
                        }

                    } else if (input.startsWith("send")) {
                        // see if the tokens in the command are even valid
                        if (tokens.length < 3) {
                            this.output.println("Error: invalid send syntax.");
                        } else if (!this.authenticated) {
                            // make sure that the user is logged in
                            this.output.println("Denied. Please login first");
                        } else {
                            // untokenize - leave out the command and user and get the rest of the tokens an append
                            // them together to make the message
                            String message = "";
                            for (String part : Arrays.copyOfRange(tokens, 2, tokens.length)) {
                                message += part + " ";
                            }
                            // send a message to the user specified
                            if (!send(this.username, tokens[1], message)) {
                                this.output.println("Error: could not broadcast.  Client not online or invalid.");
                            }
                        }

                    } else if (input.startsWith("who")) {
                        // see if the tokens in the command are even valid
                        if (tokens.length != 1) {
                            this.output.println("Error: invalid who syntax.");
                        } else if (!this.authenticated) {
                            // make sure that the user is logged in
                            this.output.println("Denied. Please login first");
                        } else {
                            // output a list of all the clients connected to the server
                            this.output.println(clients.keySet().toString());
                        }

                    } else if (input.startsWith("logout")) {
                        // see if the tokens in the command are even valid
                        if (tokens.length != 1) {
                            this.output.println("Error: invalid logout syntax.");
                        } else if (!this.authenticated) {
                            // make sure that the user is logged in
                            this.output.println("Denied. Please login first");
                        } else {
                            // logout the client from the server
                            System.out.println(this.username + " logged out");
                            this.output.println("Logging out...");
                            logout(this.username);
                            break;
                        }

                    } else if (input.startsWith("register")) {
                        if (tokens.length != 3) {
                            this.output.println("Error: invalid register syntax.");
                        } else if (this.authenticated) {
                            this.output.println("Error: you already have an account.");
                        } else {
                            if (register(tokens[1], tokens[2])) {
                                this.output.println("An account with the username " + tokens[1] + " has been created.");
                            } else {
                                this.output.println("Error: we couldn't register your account.  The username may already be in use.");
                            }
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
                    // try to log the user out again just in case they exited the client using something other than
                    // the logout command (i.e. ^C)
                    logout(this.username);
                } catch (IOException e) {
                    System.out.println("Error: exception occurred when closing the socket.  Possibly already closed?");
                }
            }
        }
    }
}
