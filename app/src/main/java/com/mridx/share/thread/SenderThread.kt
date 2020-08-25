package com.mridx.share.thread

import android.util.Log
import com.mridx.share.data.AppData
import com.mridx.share.data.FileSendData
import com.mridx.share.data.FileSenderData
import com.mridx.share.data.MusicData
import com.mridx.share.utils.FileSenderType
import java.io.*
import java.net.Socket

class SenderThread(private val fileSenderData: FileSenderData, private val socket: Socket) : Thread() {

    companion object {
        var dataType: String? = null
    }

    override fun run() {
        super.run()

        val files = getAllFiles()

        sendFiles(files)

    }

    private fun sendFiles(files: ArrayList<FileSendData>) {
        try {
            val bos = BufferedOutputStream(socket.getOutputStream())
            val dos = DataOutputStream(bos)

            dos.writeUTF(dataType) //write data type
            dos.writeInt(files.size) //total files number

            for (fileData in files) {
                val file = File(fileData.path)
                dos.writeLong(file.length()) //file size

                dos.writeUTF(file.path) //file path

                dos.writeUTF(fileData.name) //file name

                val fileInputStream = FileInputStream(file)
                val bufferedInputStream = BufferedInputStream(fileInputStream)

                var theByte: Int = 0

                while (bufferedInputStream.read().also {
                            theByte = it
                        } != -1) {
                    bos.write(theByte)
                    Log.d("kaku", "sendFiles: ${fileData.name} $theByte")
                }
                bufferedInputStream.close()

            }

            dos.close()

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getAllFiles(): ArrayList<FileSendData> {
        return when (fileSenderData.fileSenderType) {
            FileSenderType.APP -> getApps(fileSenderData.list)
            FileSenderType.MUSIC -> getMusic(fileSenderData.list)
            else -> getApps(fileSenderData.list)
        }
    }

    private fun getMusic(list: java.util.ArrayList<Any>): java.util.ArrayList<FileSendData> {
        dataType = "music"
        val dataList: ArrayList<FileSendData> = ArrayList()
        for (musicData: MusicData in list as ArrayList<MusicData>) {
            dataList.add(FileSendData(musicData.path, musicData.title))
        }
        return dataList
    }

    private fun getApps(list: ArrayList<Any>): ArrayList<FileSendData> {
        dataType = "app"
        val applist: ArrayList<FileSendData> = ArrayList()
        for (appData: AppData in list as ArrayList<AppData>) {
            applist.add(FileSendData(appData.apkPath, appData.appName))
        }
        return applist
    }

}