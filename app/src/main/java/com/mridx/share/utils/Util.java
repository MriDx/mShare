package com.mridx.share.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;

public class Util {

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
