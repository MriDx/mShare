package com.mridx.share.thread

import com.mridx.share.callback.SenderCallback
import com.mridx.share.data.FileSenderData
import com.mridx.share.data.Utils
import java.io.IOException
import java.net.Socket

class FileSender(private val fileSenderData: FileSenderData) : Thread(), (String) -> Unit, () -> Unit {

    var fileSenderCallback: SenderCallback? = null

    override fun run() {
        super.run()
        try {
            val socket: Socket = when (fileSenderData.type) {
                Utils.TYPE.CLIENT -> Socket(Utils.HOST_IP, Utils.HOST_PORT)
                else -> Socket(fileSenderData.ip, Utils.CLIENT_PORT)
            }
            fileSenderCallback?.setOnSenderCallback(true, null)

            val senderThread = SenderThread(fileSenderData, socket)
            senderThread.start()
            senderThread.onProgress = this
            senderThread.onComplete = this

        } catch (e: IOException) {
            e.printStackTrace()
            fileSenderCallback?.setOnSenderCallback(false, e)
        }

    }

    override fun invoke(p1: String) {
        fileSenderCallback?.onFileSendingProgress(p1)
    }

    override fun invoke() {
        fileSenderCallback?.setOnFileTransferCallback()
    }


}