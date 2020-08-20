package com.mridx.share.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.mridx.share.R
import com.mridx.share.data.AppData

class AppAdapterNew() : RecyclerView.Adapter<AppAdapterNew.MyViewHolder>() {

    private var appList = ArrayList<AppData>()
    private var appListSelected = ArrayList<AppData>()
    private val SELECTED = 0
    private val NORMAL: Int = 1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return if (viewType == SELECTED)
            MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.app_view_selected, null))
        else
            MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.app_view, null))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(appList[position])
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (appList[position].isSelected)
            SELECTED
        else
            NORMAL
    }

    fun setAllChecked(b: Boolean) {
        appListSelected.clear()
        for (i in appList.indices) {
            val appData = appList[i]
            appData.isSelected = b
            appListSelected.add(appData)
        }
        notifyDataSetChanged()
        if (!b) appListSelected.clear()
    }




    fun setAppList(appList: ArrayList<AppData>) {
        this.appList = appList
        notifyDataSetChanged()
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val appNameView: MaterialTextView = itemView.findViewById(R.id.appName)
        private val appSizeView: MaterialTextView = itemView.findViewById(R.id.appSize)
        private val appIconView: ShapeableImageView = itemView.findViewById(R.id.appIconView)

        fun bind(appData: AppData) {
            appNameView.text = appData.appName
            appSizeView.text = appData.apkSize
            appIconView.setImageDrawable(appData.appIcon)
        }

    }
}