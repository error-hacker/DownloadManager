package com.devsahil.downloadmanager

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.devsahil.downloadmanager.databinding.ActivityMainBinding


class MainActivity() : AppCompatActivity(), ButtonFunctions {

    private lateinit var binding: ActivityMainBinding
    private var downloadID: Long = 0
    private val storagePath: String? = Environment.DIRECTORY_DOWNLOADS //+ "/Krishi Reports"

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
        list.add("https://www.clickdimensions.com/links/TestPDFfile.pdf")
        list.add("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
        list.add("https://file-examples-com.github.io/uploads/2017/10/file-example_PDF_1MB.pdf")
        list.add("https://www.vu.edu.au/sites/default/files/campuses-services/pdfs/sample-research-report.pdf")
        list.add("https://file-examples-com.github.io/uploads/2017/10/file-sample_150kB.pdf")


        return list
    }

    override fun onDownloadButtonClicked(url: String, fileName: String) {

        if(FileFuns().checkFileExists(storagePath, fileName)) {
            Toast.makeText(applicationContext, "File Already Exists", Toast.LENGTH_LONG).show()

        } else {
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

            val request = DownloadManager.Request(Uri.parse(url))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE) // Visibility of the download Notification
                .setTitle(fileName) // Title of the Download Notification
                .setDestinationInExternalPublicDir(storagePath, fileName)
                //.setDescription("Downloading") // Description of the Download Notification


            downloadID = downloadManager.enqueue(request)

            downloadStatus(downloadID, downloadManager)

        }
    }

    @SuppressLint("Range")
    private fun downloadStatus(downloadID: Long, downloadManager: DownloadManager) {

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
                        Toast.makeText(this, "Download Failed", Toast.LENGTH_SHORT)
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
                        Toast.makeText(this,
                            "Download Completed",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    override fun onOpenButtonClicked(fileName: String) {
        if(FileFuns().checkFileExists(storagePath, fileName)) {
            FileFuns().openPDF(storagePath,fileName, this)
        }
        else{
            Toast.makeText(this, "Report not downloaded", Toast.LENGTH_LONG)
                .show()
        }
    }
}