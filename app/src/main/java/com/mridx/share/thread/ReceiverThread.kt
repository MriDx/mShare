package com.mridx.share.thread

import android.os.Environment
import android.util.Log
import java.io.*
import java.net.Socket

class ReceiverThread(private val socket: Socket) : Thread() {

    companion object {
        val extStorage = Environment.getExternalStorageDirectory()
    }

    var onProgress: ((String) -> Unit)? = null
    var onComplete: (() -> Unit)? = null

    /**
     *
     * it saves all files from input connection
     *
     */

    override fun run() {
        super.run()
        try {
            if (socket.getInputStream() != null) {
                saveFiles()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun saveFiles() {

        val bufferedInputStream = BufferedInputStream(socket.getInputStream())
        val dataInputStream = DataInputStream(bufferedInputStream)

        val dataType: String = dataInputStream.readUTF() //read data type
        val totalFiles: Int = dataInputStream.readInt() //read files number
        val files = arrayOfNulls<File>(totalFiles)

        for (i in 0 until totalFiles) {
            val length = dataInputStream.readLong() //file size
            val path = dataInputStream.readUTF().replace("./", "/") //file path
            val name = dataInputStream.readUTF() //file name
            val dir = File("${extStorage}/${dataType}/mshare", path)
            if (!dir.exists()) dir.mkdirs()
            files[i] = File(dir, name)
            val fileOutputStream = FileOutputStream(files[i])
            val bufferOutputStream = BufferedOutputStream(fileOutputStream)

            for (j in 0 until length) {
                bufferOutputStream.write(bufferedInputStream.read())
            }
            bufferOutputStream.close()
            onProgress?.invoke(name)
            Log.d("kaku", "saveFiles: download complete, $name")
        }
        Log.d("kaku", "saveFiles: download complete total $totalFiles files")
        onComplete?.invoke()

    }
}