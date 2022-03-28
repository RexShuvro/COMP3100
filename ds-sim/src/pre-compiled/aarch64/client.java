import java.net.*;
import java.io.*;

class client {
    public static void main(String[] args) throws Exception {
        Socket s = new Socket("localhost", 50000);


        BufferedReader bfr = new BufferedReader(new InputStreamReader(s.getInputStream()));
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        String serverResponse = "";

        sendtoServer(dos, "HELO");
        responseCheck(readServer(bfr), "OK");

        sendtoServer(dos, "AUTH Souvik");
        responseCheck(readServer(bfr), "OK");

        while(true){
            sendtoServer(dos, "REDY");
            serverResponse = readServer(bfr);

            sendtoServer(dos, "QUIT");
            if(responseCheck(readServer(bfr), "QUIT")){
                System.out.println("Program exited successfully");
                break;
            }
        }


        dos.close();
        s.close();
    }


    private static void sendtoServer(DataOutputStream dos, String str) throws IOException {
        dos.flush();
        dos.write((str +"\n").getBytes());

    }

    private static String readServer(BufferedReader bfr) throws IOException {
        String str = bfr.readLine();
        System.out.println("Server says: " + str);

        return str;
    }


    private static boolean responseCheck(String serverResponse, String expected){
         if (!serverResponse.equals(expected)){
             System.out.println("");
             System.exit(1);
         }
         return true;
    }
}
