# cs4850-chat-server

**To run the client-server application:**

1. Compile the java files
  - javac Server.java
  - javac Client.java
2. Run the files
  - java Server
  - java Client

**To debug in the case of an unexpected crash:**

1. Exists saying the port is in use
  - In terminal run, lsof -i:{port_number} where for this project the port number is 17388
  - If there is a process using the port, note the PID, and then run kill -9 {PID}

**Valid Commands**
- help
   View a list of valid commands.

- login UserId Password
   Attempt to login as UserId with Password.

- send all Message
   Send the entire chat room a Message.

- send UserId Message
   Send the UserID a Message.

- who
   View the current UserIDs connected to the server.

- logout
   Logout from the chat room.