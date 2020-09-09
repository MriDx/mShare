package com.mridx.share.thread

import android.util.Log
import com.mridx.share.data.*
import com.mridx.share.utils.FileSenderType
import java.io.*
import java.net.Socket

class SenderThread(private val fileSenderData: FileSenderData, private val socket: Socket) : Thread() {

    companion object {
        var dataType: String? = null
    }

    var onProgress: ((String) -> Unit)? = null
    var onComplete: (() -> Unit)? = null

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

                val buf = ByteArray(1024)
                var len: Int = 0;
                while (bufferedInputStream.read(buf).also { len = it } != -1) {
                    bos.write(buf, 0, len)
                    //onProgress?.invoke(fileData.name)
                    Log.d("kaku", "sendFiles: ${fileData.name} $len")
                }

                /*while (bufferedInputStream.read().also {
                            theByte = it
                        } != -1) {
                    bos.write(theByte)
                    //onProgress?.invoke(fileData.name)
                    Log.d("kaku", "sendFiles: ${fileData.name} $theByte")
                }*/
                bufferedInputStream.close()

            }

            dos.close()
            onComplete?.invoke()

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
            FileSenderType.VIDEO -> getVideos(fileSenderData.list)
            FileSenderType.FOLDER -> getFolders(fileSenderData.list)
            else -> getApps(fileSenderData.list)
        }
    }


    private fun getFolders(list: java.util.ArrayList<Any>): java.util.ArrayList<FileSendData> {
        TODO("Not yet implemented")
    }

    private fun getVideos(list: java.util.ArrayList<Any>): java.util.ArrayList<FileSendData> {
        dataType = "video"
        val datalist: ArrayList<FileSendData> = ArrayList()
        for (videoData: VideoData in list as ArrayList<VideoData>) {
            datalist.add(FileSendData(videoData.path, videoData.title))
        }
        return datalist
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