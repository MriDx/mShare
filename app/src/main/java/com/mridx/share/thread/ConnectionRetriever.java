package com.mridx.share.thread;

import com.mridx.share.thread.callback.OnConnectionReceived;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionRetriever extends Thread {

    OnConnectionReceived onConnectionReceived;

    public void setOnConnectionReceived(OnConnectionReceived onConnectionReceived) {
        this.onConnectionReceived = onConnectionReceived;
    }

    private ServerSocket serverSocket;
    private Socket client;

    public ConnectionRetriever(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        super.run();
        if (serverSocket != null) {
            try {
                do {
                    client = serverSocket.accept();
                    if (client.getInputStream() != null) {
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(client.getInputStream());
                        DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
                        String clientIp = dataInputStream.readUTF(); //read ip
                        int clientPort = dataInputStream.readInt(); //read port
                        onConnectionReceived.onReceived(true, clientIp, clientPort);
                        client.close();
                    }
                } while (true);
            } catch (IOException e) {
                e.printStackTrace();
                onConnectionReceived.onReceived(false, null, 0);
            }
        }
    }
}
