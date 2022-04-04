import java.net.*;
import java.io.*;

class client {

    public static void main(String[] args) throws Exception {

        Socket s = new Socket("localhost", 50000);

        BufferedReader bfr = new BufferedReader(new InputStreamReader(s.getInputStream()));
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        sendtoServer(dos, "HELO");
        responseCheck(readServer(bfr), "OK");

        sendtoServer(dos, "AUTH Souvik");
        responseCheck(readServer(bfr), "OK");

        while (true) {
            sendtoServer(dos, "REDY");

            String serverData = readServer(bfr);
            if (serverData.equalsIgnoreCase("NONE")) {
                break;
            }

            String[] data = serverData.split(" ");
            String command = data[0];
            int jobNo = Integer.parseInt(data[2]);
            System.out.println(jobNo);
            sendtoServer(dos, "GETS All");

            serverData = readServer(bfr);
            data = serverData.split(" ");

            int noServers = Integer.parseInt(data[1]);

            sendtoServer(dos, "OK");
            int[] count = new int[1];
            String serverName = serverArray(bfr, dos, noServers, count);
            System.out.println("serverName " + serverName);
            sendtoServer(dos, "OK");
            String tempResponse = readServer(bfr);
            System.out.println("Server says: " + tempResponse);

            int totalServers = count[0];
            System.out.println("Server no: " + totalServers);

            int serverNo = jobNo % totalServers;
            if (command.equals("JOBN")) {
                schd(dos, serverNo, jobNo, serverName);

                responseCheck(readServer(bfr), "OK");

            }

        }
        sendtoServer(dos, "QUIT");
        if (responseCheck(readServer(bfr), "QUIT")) {
            System.out.println("Program exited successfully");
        } else {
            System.out.println("Program exited unsuccessfully");

        }

        dos.close();
        s.close();
    }

    public static void sendtoServer(DataOutputStream dos, String str) throws IOException {
        dos.flush();
        dos.write((str + "\n").getBytes());
        dos.flush();
    }

    private static String readServer(BufferedReader bfr) throws IOException {
        String str = bfr.readLine();
       

        return str;
    }

    public static void schd(DataOutputStream dos, int serverNo, int jobNo, String serverName) throws IOException {
        sendtoServer(dos, String.format("SCHD %s %s %d", jobNo, serverName, serverNo));
    }

    public static String serverArray(BufferedReader bfr, DataOutputStream dos, int noServers, int[] count)
            throws IOException {
        String serverName = "";
        int maxCount = 0;
        int maxCores = -1;

        for (int i = 0; i < noServers; i++) {
            String str = bfr.readLine();
            if (str == null || str.isEmpty()) {
                break;
            }
           
            String[] details = str.split(" ");

            int currentCore = Integer.parseInt(details[4]);
            if (currentCore > maxCores) {
                maxCores = currentCore;
                serverName = details[0];
                maxCount = 1;
            } else if (serverName.equals(details[0])) {
                maxCount++;
            }
        }

        count[0] = maxCount;

        return serverName;

    }

    private static boolean responseCheck(String serverResponse, String expected) {
        if (!serverResponse.equals(expected)) {
            System.out.println("Communication Error");
            System.exit(1);
        }
        return true;
    }
}
