package com.mridx.share.adapter

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
import java.util.*
import kotlin.collections.ArrayList

class AudioAdapter() : RecyclerView.Adapter<AudioAdapter.myViewHolder>(), Filterable {

    private var musicList = ArrayList<MusicData>()
    private var musicListFiltered = ArrayList<MusicData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.music_view, parent, false)
        return myViewHolder(view)
    }

    override fun getItemCount(): Int {
        return musicListFiltered.size
    }

    fun setMusicList(musicList: ArrayList<MusicData>) {
        this.musicList = musicList
        this.musicListFiltered = musicList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        return holder.bind(musicListFiltered[position])
    }

    class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.musicNameView)
        private val albumArt: ImageView = itemView.findViewById(R.id.musicThumbView)
        private val size: TextView = itemView.findViewById(R.id.audioSize)

        fun bind(musicInfo: MusicData) {
            title.text = musicInfo.title
            size.text = musicInfo.audioSize
            Glide.with(itemView.context).load(musicInfo.albumArt).placeholder(R.drawable.ic_music).into(albumArt)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val query = p0.toString()
                if (query.isEmpty())
                    musicListFiltered = musicList
                else {
                    var resultList: ArrayList<MusicData> = ArrayList()
                    for (music in musicList) {
                        if (music.title.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)))
                        resultList.add(music)
                    }
                    musicListFiltered = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = musicListFiltered
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                musicListFiltered = p1?.values as ArrayList<MusicData>
                notifyDataSetChanged()
            }

        }
    }

}