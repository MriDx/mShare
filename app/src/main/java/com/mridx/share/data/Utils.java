package com.mridx.share.data;

public class Utils {


    public static String HOST_IP = "192.168.43.1", CLIENT_IP = null;
    public static final int HOST_PORT = 7575, CONNECT_HOST_PORT = 7574;/*CLIENT_PORT = HOST_PORT + 1;*/
    public static int CLIENT_PORT = HOST_PORT + 1, CONNECT_CLIENT_PORT = 7573;

    public enum TYPE {
        HOST, CLIENT
    }


}
