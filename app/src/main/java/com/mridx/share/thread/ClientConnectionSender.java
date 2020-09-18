package com.mridx.share.thread;

import android.util.Log;

import com.mridx.share.thread.callback.OnConnectionEst;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientConnectionSender extends Thread {

    OnConnectionEst onConnectionEst;

    public void setOnConnectionEst(OnConnectionEst onConnectionEst) {
        this.onConnectionEst = onConnectionEst;
    }

    private Socket socket;
    private String ip;
    private int port;

    public ClientConnectionSender(Socket socket, String ip, int port) {
        this.socket = socket;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        super.run();
        if (socket != null) {
            Log.e("kaku", "run: ahisi kela bal t");
            try {
                BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
                DataOutputStream dos = new DataOutputStream(bos);

                dos.writeUTF(ip);
                dos.writeInt(port);
                dos.close();
                onConnectionEst.onConnect(true);
            } catch (IOException e) {
                e.printStackTrace();
                onConnectionEst.onConnect(false);
            }
        }
    }
}
