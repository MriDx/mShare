package com.mridx.share.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mridx.share.R
import com.mridx.share.adapter.VideoAdapter
import com.mridx.share.data.VideoData
import kotlinx.android.synthetic.main.music_fragment.*
import kotlinx.android.synthetic.main.music_fragment.view.*
import kotlinx.android.synthetic.main.video_fragment.*
import kotlinx.android.synthetic.main.video_fragment.btmView
import java.text.DecimalFormat

class VideoFragment : Fragment(), (ArrayList<VideoData>) -> Unit {

    val GB = (1024 * 1024 * 1024).toDouble()
    val MB = (1024 * 1024).toDouble()
    val KB = 1024.toDouble()
    val df = DecimalFormat("#.##")

    lateinit var videoAdapter: VideoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.video_fragment, container, false)
        val videoHolder: RecyclerView = view.findViewById(R.id.videoHolder)
        videoAdapter = VideoAdapter()
        videoAdapter.onSelected = this
        videoHolder.apply {
            setHasFixedSize(true)
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }
        videoAdapter.setVideoList(getAllVideo(view.context))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoSearchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                videoAdapter.filter.filter(p0)
            }

        })

    }

    @SuppressLint("Recycle")
    private fun getAllVideo(context: Context): ArrayList<VideoData> {
        val videoList = ArrayList<VideoData>()
        val contentResolver = context.contentResolver
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI


        val cursor = contentResolver.query(
                uri,
                null,
                null,
                null,
                null
        )
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val title = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
                val videoTitle = cursor.getString(title)

                val videoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                Log.d("nihal", videoPath)

                val size = cursor.getColumnIndex(MediaStore.Video.Media.SIZE)
                val videoSize = getVideoSize(cursor.getString(size))

                videoList.add(VideoData(videoTitle, videoPath, videoSize ?: "0", videoPath, false))

            } while (cursor.moveToNext())
        }
        return videoList
    }

    private fun getVideoSize(videoSize: String): String? {
        val myValue = videoSize.toDouble()
        return when {
            myValue > GB -> {
                df.format(myValue / GB) + " GB"
            }
            myValue > MB -> {
                df.format(myValue / MB) + " MB"
            }
            else -> df.format(myValue / KB) + " KB"
        }
    }

    override fun invoke(p1: ArrayList<VideoData>) {
        if (p1.size > 0) {
            btmView.visibility = View.VISIBLE
            btmView.sendBtn.text = "Send ( ${p1.size} )"
        } else {
            btmView.visibility = View.GONE
        }
    }
}