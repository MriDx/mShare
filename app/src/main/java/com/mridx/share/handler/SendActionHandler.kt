package com.mridx.share.handler

import com.mridx.share.data.FileSenderData
import com.mridx.share.data.Utils
import com.mridx.share.thread.FileSender
import com.mridx.share.utils.FileSenderType

class SendActionHandler {

    companion object {
        fun send(list: ArrayList<Any>, type: FileSenderType) {

            val fileSenderData = FileSenderData(list, type, Utils.TYPE.HOST, null);
            FileSender(fileSenderData).start()

        }
    }

}