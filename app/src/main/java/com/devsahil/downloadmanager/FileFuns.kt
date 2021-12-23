package com.devsahil.downloadmanager

import android.content.Intent
import android.net.Uri
import android.os.Environment
import java.io.File

class FileFuns {

    fun checkFileExists(storagePath: String?, fileName: String): Boolean {
        val applicationFile = File(Environment.getExternalStoragePublicDirectory(storagePath), fileName)
        return applicationFile.exists()
    }
    fun openPDF(storagePath: String?, fileName: String): Intent{
        val applicationFile = File(Environment.getExternalStoragePublicDirectory(storagePath), fileName)
        val uri: Uri = Uri.fromFile(applicationFile).normalizeScheme()
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(uri, "application/pdf");
        return intent
    }
}