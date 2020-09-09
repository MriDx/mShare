package com.mridx.share.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mridx.share.R
import com.mridx.share.data.MusicData
import com.mridx.share.data.VideoData
import kotlinx.android.synthetic.main.video_view.view.*
import java.util.*
import kotlin.collections.ArrayList

class VideoAdapter : RecyclerView.Adapter<VideoAdapter.ViewHolder>(), Filterable {

    private var videoList : ArrayList<VideoData> = ArrayList()
    private var videoListFiltered : ArrayList<VideoData> = ArrayList()
    private var videoListSelected : ArrayList<VideoData> = ArrayList()

    var onSelected : ((ArrayList<VideoData>) -> Unit)? = null

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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.videoName)
        private val size: TextView = itemView.findViewById(R.id.videoSize)
        private val thumb: ImageView = itemView.findViewById(R.id.videoThumb)
        private val checkView: ConstraintLayout = itemView.findViewById(R.id.checkView)

        fun bind(videoData: VideoData) {
            title.text = videoData.title
            size.text = videoData.videoSize

            val filePath = videoData.thumbnail
            Log.d("nihal1", filePath)

            if (videoData.selected) {
                checkView.videoCheckBox.setImageResource(R.drawable.ic_selected)
            } else{
                checkView.videoCheckBox.setImageResource(R.drawable.custom_checkbox)
            }

            Glide.with(itemView.context)
                    .asBitmap()
                    .fitCenter()
                    .placeholder(R.drawable.ic_video)
                    .load(filePath)
                    .into(thumb)
            checkView.setOnClickListener { kotlin.run {
                videoData.selected = !videoData.selected
                notifyDataSetChanged()
                addToSelected()
            } }
        }
    }

    private fun addToSelected() {
        videoListSelected.clear()
        for (v in videoListFiltered) {
            if (v.selected) videoListSelected.add(v)
        }
        onSelected?.invoke(videoListSelected)
    }

    fun getSelectedList(): ArrayList<VideoData> = this.videoListSelected

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val query = p0.toString()
                if (query.isEmpty())
                    videoListFiltered = videoList
                else {
                    val resultList: ArrayList<VideoData> = ArrayList()
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