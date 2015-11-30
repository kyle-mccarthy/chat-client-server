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
