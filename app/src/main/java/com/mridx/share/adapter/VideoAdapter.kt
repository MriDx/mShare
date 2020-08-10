package com.mridx.share.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mridx.share.R
import com.mridx.share.data.MusicData
import com.mridx.share.data.VideoData
import java.util.*
import kotlin.collections.ArrayList

class VideoAdapter : RecyclerView.Adapter<VideoAdapter.ViewHolder>(), Filterable {

    private var videoList : ArrayList<VideoData> = ArrayList()
    private var videoListFiltered : ArrayList<VideoData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.video_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(videoListFiltered[position])
    }

    override fun getItemCount() = videoListFiltered.size

    fun setVideoList(videoList: ArrayList<VideoData>) {
        this.videoList = videoList
        this.videoListFiltered = videoList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.videoName)
        private val size: TextView = itemView.findViewById(R.id.videoSize)
        private val thumb: ImageView = itemView.findViewById(R.id.videoThumb)

        fun bind(videolist: VideoData) {
            title.text = videolist.title
            size.text = videolist.videoSize

            val filePath = videolist.thumbnail
            Log.d("nihal1", filePath)

            Glide.with(itemView.context)
                    .asBitmap()
                    .fitCenter()
                    .placeholder(R.drawable.ic_video)
                    .load(filePath)
                    .into(thumb)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val query = p0.toString()
                if (query.isEmpty())
                    videoListFiltered = videoList
                else {
                    var resultList: ArrayList<VideoData> = ArrayList()
                    for (video in videoList) {
                        if (video.title.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)))
                        resultList.add(video)
                    }
                    videoListFiltered = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = videoListFiltered
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                videoListFiltered = p1?.values as ArrayList<VideoData>
                notifyDataSetChanged()
            }

        }
    }


}