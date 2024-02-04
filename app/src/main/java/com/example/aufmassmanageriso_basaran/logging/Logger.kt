package com.example.aufmassmanageriso_basaran.logging

import android.util.Log
import org.json.JSONObject
import java.io.File

class Logger(val tag: String) {

    private val identicalLengthTag = with(tag.padEnd(20, ' ')) {
        if (length > 20) substring(0, 17) + "..."
        else this
    }

    fun v(msg: String, tr: Throwable? = null) {
        Log.v(tag, msg, tr)
        logToFile("V", msg, tr)
    }

    fun d(msg: String, tr: Throwable? = null) {
        Log.d(tag, msg, tr)
        logToFile("D", msg, tr)
    }

    fun i(msg: String, tr: Throwable? = null) {
        Log.i(tag, msg, tr)
        logToFile("I", msg, tr)
    }

    fun w(msg: String, tr: Throwable? = null) {
        Log.w(tag, msg, tr)
        logToFile("W", msg, tr)
    }

    fun e(msg: String, tr: Throwable? = null) {
        Log.e(tag, msg, tr)
        logToFile("E", msg, tr)
    }
    
    fun logObject(identifier: List<String>, obj: Map<String, Any?>) {
        if (isInitialized) {
            val timestamp = Timestamper.namingFormat.getNow()
            val filename = (identifier + timestamp).joinToString(separator = "_" ) + ".json"
            i("Logging object to file: $filename")
            val backupFile = File(backupDir, filename)
            backupFile.mkdirs()
            backupFile.createNewFile()
            val json = JSONObject(obj)
            backupFile.writeText(json.toString())
        }
    }

    private fun logToFile(level: String, msg: String, tr: Throwable? = null) {
        if (isInitialized) {
            val timestamp = Timestamper.contentFormat.getNow()
            logFile.appendText("$timestamp | $identicalLengthTag | $level | $msg\n")
            tr?.let { logFile.appendText(it.stackTraceToString()) }
        }
    }

    companion object {

        var isInitialized = false
            private set
        private lateinit var logDir: File
        private lateinit var logFile: File
        private lateinit var backupDir: File
        private lateinit var logger: Logger

        fun init(filesDir: File) {
            logDir = File(filesDir, "logs").also { it.mkdirs() }
            backupDir = File(filesDir, "backup").also { it.mkdirs() }

            val timestamp = Timestamper.namingFormat.getNow()
            logFile = File(logDir, "isoBa_$timestamp.log")
            logFile.createNewFile()
            isInitialized = true

            logger = Logger("Logger")
            logger.i("Logger initialized. Log file: ${logFile.absolutePath}")
        }

        fun getPathToFiles(): Map<String, File> {
            if (!isInitialized) return emptyMap()

            val result = mutableMapOf<String, File>()
            for (file in logDir.listFiles()!!) result["logs/"+file.name] = file
            for (file in backupDir.listFiles()!!) result["backup/"+file.name] = file
            return result
        }
    }
}