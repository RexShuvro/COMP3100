import java.net.*;

import com.sun.tools.javac.Main;

import java.io.*;

import java.util.ArrayList;

import java.util.Scanner;

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
            jobArray(bfr, dos);

            sendtoServer(dos, "GETS All");
            String serverData = readServer(bfr);
            String[] data = serverData.split(" ");
            int noServers = Integer.parseInt(data[1]);

            sendtoServer(dos, "OK");
            String[] ans = serverArray(bfr, dos, noServers);

            ;
            System.out.println("Server name: " + ans[0]);
            System.out.println("Server number: " + ans[1]);

            sendtoServer(dos, "OK");

            sendtoServer(dos, "QUIT");
            if (responseCheck(readServer(bfr), "QUIT")) {
                System.out.println("Program exited successfully");
                break;
            }
        }

        dos.close();
        s.close();
    }

    public static void sendtoServer(DataOutputStream dos, String str) throws IOException {
        dos.flush();
        dos.write((str + "\n").getBytes());

    }

    private static String readServer(BufferedReader bfr) throws IOException {
        String str = bfr.readLine();
        System.out.println("Server says: " + str);

        return str;
    }

    public static void jobArray(BufferedReader bfr, DataOutputStream dos) throws IOException {
        String in = bfr.readLine();

        String str = in.replaceAll("[^\\d]", " ");

        str = str.trim();

        str = str.replaceAll(" +", " ");

        String s[] = str.split(" ");
        String out[] = new String[s.length];

        for (int i = 0; i < s.length; i++) {

            out[i] = (s[i]);
        }
    }

    public static String[] serverArray(BufferedReader bfr, DataOutputStream dos, int noServers) throws IOException {
        String serverName = "";
        int maxCores = -1;
        int serverno = -2;

        String[] ans = new String[2];
        for (int i = 0; i < noServers; i++) {
            String str = bfr.readLine();
            if (str == null || str.isEmpty()) {
                break;
            }
            // juju 0 inactive -1 2 4000 16000 0 0
            String[] details = str.split(" ");

            String out[] = new String[details.length];

            for (int k = 0; k < details.length; k++) {

                out[k] = (details[k]);
            }

            int currentCore = Integer.parseInt(details[4]);
            if (currentCore > maxCores) {
                maxCores = currentCore;
                serverName = details[0];
            }

            for (int j = 0; j < out.length; j++) {
                if (serverName == out[j]) {
                    serverno++;
                }

            }

        }

        String no = String.valueOf(serverno);

        ans[0] = serverName;
        ans[1] = no;

        return ans;

    }

    private static boolean responseCheck(String serverResponse, String expected) {
        if (!serverResponse.equals(expected)) {
            System.out.println("Communication Error");
            System.exit(1);
        }
        return true;
    }
}
