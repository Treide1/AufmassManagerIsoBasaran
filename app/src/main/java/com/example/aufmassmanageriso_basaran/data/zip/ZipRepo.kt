package com.example.aufmassmanageriso_basaran.data.zip

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.aufmassmanageriso_basaran.logging.Logger
import com.example.aufmassmanageriso_basaran.logging.Timestamper
import com.example.aufmassmanageriso_basaran.logging.getNow
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ZipRepo {

    private val logger = Logger("ZipRepo")

    private lateinit var downloadZipResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var contentResolver: ContentResolver

    fun init(activity: ComponentActivity) {
        val contract = ActivityResultContracts.StartActivityForResult()
        downloadZipResultLauncher = activity.registerForActivityResult(contract) { result: ActivityResult ->
            logger.d("downloadZipResultLauncher: result=$result")
            if (result.resultCode == ComponentActivity.RESULT_OK) {
                // There are no request codes
                if (result.data != null) {
                    val uri = result.data!!.data!!
                    logger.i("downloadZipResultLauncher: uri=$uri")
                    val fileList = Logger.getPathToFiles()
                    val prettyFileList = fileList.keys.joinToString(", ")
                    logger.i("downloadZipResultLauncher: fileList=$prettyFileList")
                    createZipAtUri(fileList, uri)
                }
            }
        }
        contentResolver = activity.contentResolver
    }

    fun launchBackupDownloadPicker() {
        val timestamp = Timestamper.dayFormat.getNow()
        val title = "aufmassmanager_backup_$timestamp.zip"
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/zip"
            putExtra(Intent.EXTRA_TITLE, title)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse("/Download"))
            }
        }
        downloadZipResultLauncher.launch(intent)
    }

    private fun createZipAtUri(pathToFileMap: Map<String, File>, uri: Uri) {
        val BUFFER = 4096
        val pfd = contentResolver.openFileDescriptor(uri, "w")
        try {
            val dest = FileOutputStream(pfd!!.fileDescriptor)
            val out = ZipOutputStream(BufferedOutputStream(dest))
            val data = ByteArray(BUFFER)
            pathToFileMap.forEach { (relPath, file) ->
                logger.v("Adding: $relPath")
                val origin = BufferedInputStream(file.inputStream(), BUFFER)
                val entry = ZipEntry(relPath)
                out.putNextEntry(entry)
                var count: Int
                while (origin.read(data, 0, BUFFER).also { count = it } != -1) {
                    out.write(data, 0, count)
                }
                origin.close()
            }
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        pfd?.close()
    }
}