package com.devsahil.downloadmanager

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

class FileFuns {

    fun checkFileExists(storagePath: String?, fileName: String): Boolean {
        val applicationFile = File(Environment.getExternalStoragePublicDirectory(storagePath), fileName)
        return applicationFile.exists()
    }


    fun openPDF(storagePath: String?, fileName: String, context: Context) {
        val applicationFile = File(Environment.getExternalStoragePublicDirectory(storagePath), fileName)
        val uri: Uri =
            FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", applicationFile)
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG)
                .show()
        }
    }
}