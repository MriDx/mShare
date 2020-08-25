package com.mridx.share.thread

import com.mridx.share.callback.SenderCallback
import com.mridx.share.data.FileSenderData
import com.mridx.share.data.Utils
import java.io.IOException
import java.net.Socket

class FileSender(private val fileSenderData: FileSenderData) : Thread() {

    var fileSenderCallback: SenderCallback? = null

    override fun run() {
        super.run()
        try {
            val socket: Socket = when (fileSenderData.type) {
                Utils.TYPE.CLIENT -> Socket(fileSenderData.ip, Utils.CLIENT_PORT)
                else -> Socket(Utils.HOST_IP, Utils.HOST_PORT)
            }
            fileSenderCallback?.setOnSenderCallback(true, null)

            val senderThread = SenderThread(fileSenderData, socket)
            senderThread.start()

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


}