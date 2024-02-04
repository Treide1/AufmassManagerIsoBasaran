package com.example.aufmassmanageriso_basaran.logging

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
object Timestamper {

    // Example: 2024-02-04_17-57-00
    val namingFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")

    // Example: 2024-02-04 18:04:28.120
    val contentFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    // Example: 2024-02-04
    val dayFormat = SimpleDateFormat("yyyy-MM-dd")
}

fun SimpleDateFormat.getNow(): String {
    return format(Date())
}