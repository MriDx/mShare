package com.mridx.share.fragment

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.mridx.share.R
import com.mridx.share.adapter.AppAdapter
import com.mridx.share.data.AppData
import kotlinx.android.synthetic.main.app_fragment.*
import java.io.File
import java.text.DecimalFormat

class AppFragmentNew : Fragment(), (ArrayList<AppData>) -> Unit {

    private lateinit var appAdapter: AppAdapter
    lateinit var btmView: MaterialCardView

    private val MB = 1024 * 1024.toLong()
    private val KB: Long = 1024
    private val decimalFormat = DecimalFormat("#.##")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.app_fragment, container, false)
        val appsHolder: RecyclerView = view.findViewById(R.id.appsHolder)
        appAdapter = AppAdapter()
        getApps(container!!.context)
        btmView = view.findViewById(R.id.btmView)
        appsHolder.apply {
            adapter = appAdapter
            layoutManager = GridLayoutManager(context, 4)
        }
        appAdapter.setAppAdapterClicked { selectedList -> showSendBtn(selectedList.size) }
        val appCheckBox: MaterialCheckBox = view.findViewById(R.id.appCheckbox)
        appCheckBox.setOnCheckedChangeListener { _, b -> appAdapter.setAllChecked(b) }
        return view
    }

    private fun getApps(context: Context) {
        val getApps = GetApps(context)
        getApps.onComplete = this
        getApps.start()
    }

    private fun showSendBtn(size: Int) {
        when {
            size > 0 -> {
                btmView.visibility = View.VISIBLE
                appSendBtn.text = "Send ($size)"
            }
            else -> btmView.visibility = View.GONE
        }
    }

    fun installedApps(): ArrayList<AppData> {
        val appList = ArrayList<AppData>()
        val packList = context!!.packageManager.getInstalledPackages(0)
        for (i in packList.indices) {
            val packInfo = packList[i]
            if (packInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                val appName = packInfo.applicationInfo.loadLabel(context!!.packageManager).toString()
                val icon = packInfo.applicationInfo.loadIcon(context!!.packageManager)
                val apkPath = packInfo.applicationInfo.sourceDir
                appList.add(AppData(appName, icon, apkPath, getFileSize(apkPath), false))
            }
        }
        return appList
    }

    private fun getFileSize(apkPath: String): String? {
        val file = File(apkPath)
        if (file.exists()) {
            val fileSize = file.length().toDouble()
            return if (fileSize > MB) {
                decimalFormat.format(fileSize / MB) + " MB"
            } else decimalFormat.format(fileSize / KB) + " KB"
        }
        return "00 KB"
    }

    inner class GetApps(val context: Context) : Thread() {
        var onComplete: ((ArrayList<AppData>) -> Unit)? = null
        override fun run() {
            super.run()
            onComplete?.invoke(installedApps())
        }
    }

    override fun invoke(p1: ArrayList<AppData>) {
        this.activity?.runOnUiThread {
            appAdapter.setAppList(p1)
            progressBar?.visibility = View.GONE
        }

    }

}