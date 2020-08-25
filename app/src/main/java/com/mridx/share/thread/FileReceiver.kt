package com.mridx.share.thread

import com.mridx.share.data.Utils
import com.mridx.share.thread.FileReceiver.Companion.serverSocket
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class FileReceiver(private val type: Utils.TYPE) : Thread() {

    companion object {
        lateinit var serverSocket: ServerSocket
        lateinit var client: Socket
    }

    override fun run() {
        super.run()

        try {
            serverSocket = if (type == Utils.TYPE.HOST) {
                ServerSocket(Utils.HOST_PORT)
            } else {
                ServerSocket(Utils.CLIENT_PORT)
            }

            while (true) {
                client = serverSocket.accept()
                //start file saving thread
                Thread(ReceiverThread(client)).start()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}