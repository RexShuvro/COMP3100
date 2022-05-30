import java.net.*;
import java.io.*;

class new {

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
            sendtoServer(dos, "GETS"+" "+"Capable"+" "+data[4]+" "+data[5]+" "+data[6]);
            sendtoServer(dos, "OK");
            serverData = readServer(bfr);
            data = serverData.split(" ");
            String serverName = data[0];
            int serverNo = Integer.parseInt(data[1]);
            

            


            

         
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


    private static boolean responseCheck(String serverResponse, String expected) {
        if (!serverResponse.equals(expected)) {
            System.out.println("Communication Error");
            System.exit(1);
        }
        return true;
    }
}
