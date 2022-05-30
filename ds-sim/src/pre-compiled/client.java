import java.net.*;
import java.io.*;

class client {

    public static void main(String[] args) throws Exception {

        Socket s = new Socket("localhost", 50000); // creates connection with the server

        BufferedReader bfr = new BufferedReader(new InputStreamReader(s.getInputStream()));
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        sendtoServer(dos, "HELO"); // handshake
        responseCheck(readServer(bfr), "OK");

        sendtoServer(dos, "AUTH Souvik"); // sends authorization info
        responseCheck(readServer(bfr), "OK");

        while (true) {
            sendtoServer(dos, "REDY"); // Tells the server that the client is ready for a job

            String serverData = readServer(bfr); // gets the job info
            if (serverData.equalsIgnoreCase("NONE")) {
                break;
            } // Checks to see if the received command is "NONE" or "QUIT" which
              // ends the program

            String[] data = serverData.split(" "); // stores the job info
            String command = data[0];
            int jobNo = Integer.parseInt(data[2]);
            System.out.println(jobNo);
            // only execute when server says JOBN ...
            if (command.equals("JOBN")) {
                // gets capable servers with enough core memory disk
                sendtoServer(dos, "GETS Capable" + " " + data[4] + " " + data[5] + " " + data[6]);

                serverData = readServer(bfr);
                data = serverData.split(" ");

                int noServers = Integer.parseInt(data[1]);
                // Say OK to the server (ie: OK i got your msg)
                sendtoServer(dos, "OK");
                int[] count = new int[1]; // to store how many servers of largest type
                String serverName = serverArray(bfr, dos, noServers, count); // gets the largest server name and how
                                                                             // many of them
                System.out.println("serverName " + serverName);
                sendtoServer(dos, "OK");
                String tempResponse = readServer(bfr);
                System.out.println("Server says: " + tempResponse);

                int totalServers = count[0];
                System.out.println("Server no: " + totalServers);

                int serverNo = jobNo % totalServers; // to get half of round-robin
                if (command.equals("JOBN")) {
                    schd(dos, serverNo, jobNo, serverName); // scheduling

                    responseCheck(readServer(bfr), "OK");

                }

            }
        }
        sendtoServer(dos, "QUIT"); // Sends "Quit" to the server to end the session
        if (responseCheck(readServer(bfr), "QUIT")) {
            System.out.println("Program exited successfully");
        } else {
            System.out.println("Program exited unsuccessfully");

        }

        dos.close();
        s.close();
    }

    // Function used to send a msg to the server
    public static void sendtoServer(DataOutputStream dos, String str) throws IOException {
        dos.flush();
        dos.write((str + "\n").getBytes());
        dos.flush();
    }

    // Function used to read a msg from the server
    private static String readServer(BufferedReader bfr) throws IOException {
        String str = bfr.readLine();

        return str;
    }

    // scheduling function
    public static void schd(DataOutputStream dos, int serverNo, int jobNo, String serverName) throws IOException {
        sendtoServer(dos, String.format("SCHD %s %s %d", jobNo, serverName, serverNo));
    }

    // function to get the largest server and its number of occurence
    public static String serverArray(BufferedReader bfr, DataOutputStream dos, int noServers, int[] count)
            throws IOException {
        String serverName = "";
        int maxCount = 0;
        int maxCores = -1;

        for (int i = 0; i < noServers; i++) {
            String str = bfr.readLine();
            // to read the whole list
            if (str == null || str.isEmpty()) {
                break;
            }

            String[] details = str.split(" ");

            int currentCore = Integer.parseInt(details[4]);
            // gets the server with maximum cores
            if (currentCore > maxCores) {
                maxCores = currentCore;
                serverName = details[0];
                maxCount = 1;
            } else if (serverName.equals(details[0])) {
                // if the biggest server is found in the list, increase by one
                maxCount++;
            }
        }

        count[0] = maxCount;

        return serverName;
    }

    // checks if the server is responding correctly
    private static boolean responseCheck(String serverResponse, String expected) {
        if (!serverResponse.equals(expected)) {
            System.out.println("Communication Error");
            System.exit(1);
        }
        return true;
    }
}
