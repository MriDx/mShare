package com.mridx.share.thread

import com.mridx.share.callback.ReceiverCallback
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

    var receiverCallback : ReceiverCallback? = null

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
                receiverCallback?.onConnected(true, null)
                //start file saving thread
                val receiverThread = ReceiverThread(client)
                receiverThread.start()
                //Thread(ReceiverThread(client)).start()
                receiverThread.onProgress = this::onProgress
                receiverThread.onComplete = this::onComplete
            }
        } catch (e: IOException) {
            e.printStackTrace()
            receiverCallback?.onConnected(false, e)
        }

    }

    private fun onComplete() {
        receiverCallback?.onComplete()
    }

    private fun onProgress(p1: String) {
        receiverCallback?.onReceivingProgress(p1)
    }
}