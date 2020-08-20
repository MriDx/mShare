package com.mridx.share.fragment

import android.content.Context
import android.os.Bundle
import android.os.StatFs
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.mridx.share.R
import com.mridx.share.adapter.StorageAdapter
import com.mridx.share.data.FileData
import com.mridx.share.data.StorageData
import com.mridx.share.utils.StorageUtil
import kotlinx.android.synthetic.main.file_root_view.*
import kotlinx.android.synthetic.main.files_fragment.*
import java.io.File
import java.text.DecimalFormat

class FileRootFragment : Fragment(), (StorageData) -> Unit, (Boolean, ArrayList<StorageData>) -> Unit {

    lateinit var storageAdapter: StorageAdapter

    val MB = (1024 * 1024).toDouble()
    val GB = (1024 * 1024 * 1024).toDouble()
    val KB = 1024.toDouble()
    val df = DecimalFormat("#.##")

    enum class ROOT_TYPE {
        SD, IN
    }

    var onRootItemClicked: ((StorageData) -> Unit?)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.file_root_view, null, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //fileRootView.visibility = View.VISIBLE

        storageAdapter = StorageAdapter()
        getRoot(view.context)
        val lm = LinearLayoutManager(view.context)
        lm.orientation = LinearLayoutManager.VERTICAL

        storageAdapter.onStorageClicked = this
        storageHolder.apply {
            layoutManager = lm
            adapter = storageAdapter
        }
        //getData(StorageUtil.getStorageList())
        //storageAdapter.setList(getData(StorageUtil.getStorageList()))

    }

    private fun getRoot(context: Context) {
        val getRoot = GetRoot(context)
        getRoot.onComplete = this
        getRoot.start()
    }

    private fun getData(storageList: List<StorageUtil.StorageInfo>): ArrayList<StorageData> {
        val storageDataList: ArrayList<StorageData> = ArrayList()
        for (storageInfo: StorageUtil.StorageInfo in storageList) {
            val file = File(storageInfo.path.replace("/mnt/media_rw/", "/storage/"))
            val size = (StatFs(file.absolutePath).availableBytes / GB)
            val total = (StatFs(file.absolutePath).totalBytes / GB)

            storageDataList.add(StorageData(storageInfo.displayName, storageInfo.path, size, total))
        }
        return storageDataList
    }



    override fun invoke(storageData: StorageData) {
        onRootItemClicked?.invoke(storageData)
    }

    inner class GetRoot(context: Context) : Thread() {
        var onComplete : ((Boolean, ArrayList<StorageData>) -> Unit)? = null
        override fun run() {
            super.run()
            val list: ArrayList<StorageData> = getData(StorageUtil.getStorageList())
            onComplete?.invoke(true, list)
        }
    }

    override fun invoke(p1: Boolean, p2: ArrayList<StorageData>) {
        storageAdapter.setList(p2)
    }


}