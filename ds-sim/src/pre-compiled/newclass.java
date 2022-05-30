import java.io.*;
import java.net.*;

public class newclass {

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
            
            
            serverData = readServer(bfr);
            data = serverData.split(" ");
            int noServers = Integer.parseInt(data[1]);
            
            
            
            sendtoServer(dos, "OK");

            
            String [] sData = serverArray(bfr, dos, noServers);
            sendtoServer(dos, "OK");
            int serverNo = Integer.parseInt(sData[1]);
            String serverName = sData[0];
            System.out.println("Servername: "+ serverName);
            System.out.println("Serverno: "+ serverNo);
            sendtoServer(dos, "OK");
            String tempResponse = readServer(bfr);
            System.out.println("Server says: " + tempResponse);

            

         
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

    public static String [] serverArray(BufferedReader bfr, DataOutputStream dos, int noServers)
    throws IOException {
String serverName = "";
String serverNo = "";
int ser = 1;


for (int i = 0; i < noServers; i++) {
    String str = bfr.readLine();
    if (str == null || str.isEmpty()) {
        break;
    }
   
    String[] details = str.split(" ");
    if(serverName.equals(details[0])){
    serverName = details[0];
    }
System.out.println("server: " + serverName);


}
String[] arr = new String [2];
arr[0] = serverName;
arr[1] = serverNo;
return arr;

}


    private static boolean responseCheck(String serverResponse, String expected) {
        if (!serverResponse.equals(expected)) {
            System.out.println("Communication Error");
            System.exit(1);
        }
        return true;
    }

}