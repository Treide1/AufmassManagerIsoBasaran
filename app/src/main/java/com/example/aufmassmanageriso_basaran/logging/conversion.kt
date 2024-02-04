package com.example.aufmassmanageriso_basaran.logging

fun HashMap<String, Any?>.replaceZeitstempel(): Map<String, Any?> {
    val clone = hashMapOf<String, Any?>()
    clone.putAll(this)
    val timestamp = Timestamper.namingFormat.getNow()
    clone["zeitstempel"] = timestamp
    return clone.toMap()
}