package com.mridx.share.fragment

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.mridx.share.R
import com.mridx.share.adapter.AudioAdapter
import com.mridx.share.data.AppData
import com.mridx.share.data.MusicData
import com.mridx.share.utils.FileSenderType
import kotlinx.android.synthetic.main.music_fragment.*
import kotlinx.android.synthetic.main.music_fragment.view.*
import java.text.DecimalFormat


class MusicFragment : Fragment(), (ArrayList<MusicData>) -> Unit, (Boolean, ArrayList<MusicData>) -> Unit {

    var onSendAction : ((ArrayList<Any>, FileSenderType) -> Unit)? = null

    val MB = (1024 * 1024).toDouble()
    val KB = 1024.toDouble()
    val df = DecimalFormat("#.##")
    lateinit var audioAdapter: AudioAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.music_fragment, container, false)
        val musicHolder: RecyclerView = view.findViewById(R.id.musicHolder)
        audioAdapter = AudioAdapter()
        getMusic(container!!.context)
        audioAdapter.onSelected = this
        musicHolder.apply {
            setHasFixedSize(true)
            adapter = audioAdapter
            layoutManager = LinearLayoutManager(context)
        }
        view.findViewById<MaterialButton>(R.id.sendBtn).setOnClickListener { handleSendAction() }
        //audioAdapter.setMusicList(getAllAudio(container!!.context))
        return view
    }

    private fun handleSendAction() {
        val selectedList : ArrayList<MusicData> = audioAdapter.getSelectedList()
        if (selectedList.size == 0) {
            Toast.makeText(context, "Select at least one Music", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(context, "Send selected music, Total - " + selectedList.size, Toast.LENGTH_SHORT).show()
        onSendAction?.invoke(selectedList as ArrayList<Any>, FileSenderType.MUSIC)

    }

    private fun getMusic(context: Context) {
        val getAll = GetAll(context)
        getAll.OnComplete = this
        getAll.start()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audioSearchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                audioAdapter.filter.filter(p0)
            }
        })
    }

    private fun getAlbumart(albumId: String): String {
        val albumArtUri: Uri = Uri.parse("content://media/external/audio/albumart")
        val uri: Uri = ContentUris.withAppendedId(albumArtUri, albumId.toLong())
        return uri.toString()
    }

    private fun getSongSize(songSize: String): String {
        val myValue = songSize.toDouble()
        return if (myValue > MB) {
            df.format(myValue / MB) + " MB"
        } else df.format(myValue / KB) + " KB"
    }

    override fun invoke(p1: ArrayList<MusicData>) {
        if (p1.size > 0) {
            btmView.visibility = View.VISIBLE
            btmView.sendBtn.text = "Send ( ${p1.size} )"
        } else {
            btmView.visibility = View.GONE
        }
    }

    inner class GetAll(private val context: Context) : Thread() {


        var OnComplete: ((Boolean, ArrayList<MusicData>) -> Unit)? = null

        override fun run() {
            super.run()
            val list: ArrayList<MusicData> = getMusic()
            OnComplete?.invoke(true, list)
        }

        private fun getMusic(): java.util.ArrayList<MusicData> {
            val audioList = ArrayList<MusicData>()
            val contentResolver = context.contentResolver
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val cursor = contentResolver.query(
                    uri,
                    null,
                    null,
                    null,
                    null
            )

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val title = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                    val songTitle = cursor.getString(title)

                    val size: Int = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
                    val songSize = getSongSize(cursor.getString(size))
                    //to get the path of an audio file
                    val audioPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    Log.d("nihal", audioPath)


                    val albumArt: String = getAlbumart(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)))
                    audioList.add(MusicData(songTitle, albumArt, songSize, audioPath, false))
                } while (cursor.moveToNext())
            }
            return audioList
        }
    }

    override fun invoke(p1: Boolean, p2: ArrayList<MusicData>) {
        audioAdapter.setMusicList(p2)
    }
}



