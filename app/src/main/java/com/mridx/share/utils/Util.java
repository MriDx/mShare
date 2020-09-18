package com.mridx.share.utils;

import com.mridx.share.data.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    private static String zeroTo255 = "(\\d{1,2}|(0|1)\\"
            + "d{2}|2[0-4]\\d|25[0-5])";
    private static String regex = zeroTo255 + "\\."
            + zeroTo255 + "\\."
            + zeroTo255 + "\\."
            + zeroTo255;

    private static Pattern p = Pattern.compile(regex);

    private static Util instance;
    private ServerSocket serverSocket;

    public static Util getInstance() {
        if (instance == null) {
            instance = new Util();
        }
        return instance;
    }

    public ServerSocket getServerSocket() throws IOException {
        if (serverSocket == null) {
            serverSocket = new ServerSocket(Utils.CONNECT_HOST_PORT);
        }
        return serverSocket;
    }

    public void stopServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean validateIp(String s) {
        Matcher m = p.matcher(s);
        return m.matches();
    }

    public static String getConnectedClientList() {
        String ip = "0.0.0.0";
        int clientcount = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                String mac = splitted[3];
                System.out.println("Mac : Outside If " + mac);
                if (mac.matches("..:..:..:..:..:..")) {
                    clientcount++;
                    System.out.println("Mac : " + mac + " IP Address : " + splitted[0]);
                    System.out.println("Client_count  " + clientcount + " MAC_ADDRESS  " + mac);
                    /*Toast.makeText(
                            getApplicationContext(),
                            "Client_count  " + clientcount + "   MAC_ADDRESS  "
                                    + mac, Toast.LENGTH_SHORT).show();*/
                    ip = splitted[0];
                    /*Log.d(TAG, "getConnectedClientList: " + "Mac : " + mac + " IP Address : " + splitted[0]);
                    Log.d(TAG, "getConnectedClientList: " + "Client_count  " + clientcount + " MAC_ADDRESS  " + mac);*/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ip;
        }
        //qrViewer.dismiss();
        //String serverIP = getLocalIpAddress();
        //startServer(serverIP);
        return ip;
    }

}
