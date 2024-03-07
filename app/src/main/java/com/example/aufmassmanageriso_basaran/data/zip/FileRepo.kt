package com.example.aufmassmanageriso_basaran.data.zip

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.aufmassmanageriso_basaran.data.utility.logging.Logger
import com.example.aufmassmanageriso_basaran.data.utility.timestamping.Timestamper
import com.example.aufmassmanageriso_basaran.data.utility.getNow
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object FileRepo {

    private val logger = Logger("FileRepo")

    private lateinit var downloadZipResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var exportExcelResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var contentResolver: ContentResolver
    private lateinit var lifecycleIoScope: CoroutineScope


    fun init(activity: ComponentActivity) {
        val contract = ActivityResultContracts.StartActivityForResult()
        downloadZipResultLauncher = activity.registerForActivityResult(contract, ::downloadPickerCallback)
        exportExcelResultLauncher = activity.registerForActivityResult(contract, ::exportExcelCallback)
        contentResolver = activity.contentResolver
        lifecycleIoScope = activity.lifecycleScope + Dispatchers.IO
    }

    /**
     * Asynchronously launches the system file picker to select a location to save the backup zip.
     */
    fun launchBackupDownloadPicker() {
        val timestamp = Timestamper.dayFormat.getNow()
        val title = "aufmassmanager_backup_$timestamp.zip"
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/zip"
            putExtra(Intent.EXTRA_TITLE, title)
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse("/Download"))
        }
        downloadZipResultLauncher.launch(intent)
    }

    /**
     * Callback for the downloadZipResultLauncher. This is called when the user has selected a location.
     */
    private fun downloadPickerCallback(result: ActivityResult) {
        logger.d("downloadZipResultLauncher: result=$result")
        if (result.resultCode == ComponentActivity.RESULT_OK) {
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

    /**
     * Creates a zip file at the given [uri] containing the files in the [pathToFileMap].
     */
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

    ///////////////////////////////////////////////////////////////////////////

    var createWorkbookForExport: suspend () -> HSSFWorkbook = {
        logger.e("exportExcelResultLauncher: no workbook creator set!")
        HSSFWorkbook()
    }

    fun launchExportExcelPicker(
        bauvorhabenName: String
    ) {
        val timestamp = Timestamper.dayFormat.getNow()
        val title = "aufmassmanager_${bauvorhabenName}_$timestamp"
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.ms-excel"
            putExtra(Intent.EXTRA_TITLE, title)
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse("/Download"))
        }
        exportExcelResultLauncher.launch(intent)
    }

    private fun exportExcelCallback(result: ActivityResult) {
        logger.d("exportExcelResultLauncher: result=$result")
        if (result.resultCode == ComponentActivity.RESULT_OK) {
            if (result.data != null) {
                // Perform the export in a coroutine to not block main
                lifecycleIoScope.launch(CoroutineName("exportExcelCallbackLaunch")) {
                    val uri = result.data!!.data!!
                    logger.i("exportExcelResultLauncher: uri=$uri")
                    contentResolver.openOutputStream(uri)?.use { outputStream ->
                        logger.i("exportExcelResultLauncher: writing to outputStream")
                        val workbook = createWorkbookForExport()
                        workbook.write(outputStream)
                    }
                }
            }
        }
    }
}