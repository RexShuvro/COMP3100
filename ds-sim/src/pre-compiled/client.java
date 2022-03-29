import java.net.*;

import com.sun.tools.javac.Main;

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

        while(true){
            sendtoServer(dos, "REDY");
            jobArray(bfr, dos);

           
                 


            sendtoServer(dos, "QUIT");
            if(responseCheck(readServer(bfr), "QUIT")){
                System.out.println("Program exited successfully");
                break;
            }
        }


        dos.close();
        s.close();
    }


    public static void sendtoServer(DataOutputStream dos, String str) throws IOException {
        dos.flush();
        dos.write((str +"\n").getBytes());

    }

    private static String readServer(BufferedReader bfr) throws IOException {
        String str = bfr.readLine();
        System.out.println("Server says: " + str);

        return str;
    }
 
    public static void jobArray(BufferedReader bfr, DataOutputStream dos ) throws IOException {
        String in = bfr.readLine();
        
        String str = in.replaceAll("[^\\d]", " ");

       str = str.trim(); 
      
       str = str.replaceAll(" +", " "); 

               
      String s[] = str.split(" "); 
      String out[] = new String[s.length]; 
      
      for(int i = 0 ; i < s.length ; i++){ 
      
           out[i] = (s[i]);        
           }

       sendtoServer(dos, ("GETS CAPABLE"+" "+out[3]+" "+out[4]+" "+out[5]));

       readServer(bfr);

        
    }

    private static boolean responseCheck(String serverResponse, String expected){
         if (!serverResponse.equals(expected)){
             System.out.println("Communication Error");
             System.exit(1);
         }
         return true;
    }
}
