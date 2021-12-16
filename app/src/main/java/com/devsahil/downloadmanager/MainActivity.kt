package com.devsahil.downloadmanager

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.devsahil.downloadmanager.databinding.ActivityMainBinding
import android.app.DownloadManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import androidx.browser.customtabs.CustomTabsIntent
import com.google.firebase.firestore.util.Util
import android.widget.Toast

import android.content.Intent

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.database.Cursor


class MainActivity : AppCompatActivity(), DownloadImageViewClicked {

    lateinit var binding: ActivityMainBinding
    private var downloadID: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.reportsRecyclerView.layoutManager = LinearLayoutManager(this)
        val items = fetchData()
        val adapter = ReportsListAdapter(items, this)
        binding.reportsRecyclerView.adapter = adapter

    }


    private fun fetchData(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("http://www.africau.edu/images/default/sample.pdf")
        list.add("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
        list.add("http://www.wright.edu/~david.wilson/eng3000/samplereport.pdf")
        list.add("https://www.vu.edu.au/sites/default/files/campuses-services/pdfs/sample-research-report.pdf")

        return list
    }

    @SuppressLint("Range")
    override fun onButtonClicked(url: String, fileName: String) {


        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        val request = DownloadManager.Request(Uri.parse(url))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE) // Visibility of the download Notification
            .setTitle(fileName) // Title of the Download Notification
            .setDescription("Downloading") // Description of the Download Notification
            .setDestinationInExternalPublicDir("/Krishi Reports",fileName);

        downloadID = downloadManager.enqueue(request)

        var finishDownload = false
        var progress: Int
        while (!finishDownload) {
            val cursor: Cursor =
                downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
            if (cursor.moveToFirst()) {
                val status: Int =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                when (status) {
                    DownloadManager.STATUS_FAILED -> {
                        finishDownload = true
                        Toast.makeText(this@MainActivity, "Download Failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                    DownloadManager.STATUS_PAUSED -> {}
                    DownloadManager.STATUS_PENDING -> {}
                    DownloadManager.STATUS_RUNNING -> {
                        val total: Long =
                            cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        if (total >= 0) {
                            val downloaded: Long =
                                cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                            progress = (downloaded * 100L / total).toInt()
                            // if you use downloadmanger in async task, here you can use like this to display progress.
                            // Don't forget to do the division in long to get more digits rather than double.
                            //  publishProgress((int) ((downloaded * 100L) / total));
                        }
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        progress = 100
                        // if you use aysnc task
                        // publishProgress(100);
                        finishDownload = true
                        Toast.makeText(this@MainActivity, "Download Completed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }
}