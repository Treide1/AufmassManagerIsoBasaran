package com.example.aufmassmanageriso_basaran.data.utility

import com.example.aufmassmanageriso_basaran.data.utility.timestamping.Timestamper
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

fun HashMap<String, Any?>.replaceZeitstempel(
    format: SimpleDateFormat = Timestamper.namingFormat
): Map<String, Any?> {
    val clone = hashMapOf<String, Any?>()
    clone.putAll(this)
    val timestamp = format.getNow()
    clone["zeitstempel"] = timestamp
    return clone.toMap()
}

fun SimpleDateFormat.getNow(): String {
    return format(Date())
}

fun Timestamp.toFormattedString(
    format: SimpleDateFormat = Timestamper.excelFormat
): String {
    // Convert seconds to millis and then via SimpleDateFormat
    val millis = seconds * 1_000L
    return format.format(Date(millis))
}
