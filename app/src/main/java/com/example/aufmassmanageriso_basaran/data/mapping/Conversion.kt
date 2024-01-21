package com.example.aufmassmanageriso_basaran.data.mapping

import kotlin.math.pow

fun String.toIntBlankAsZero() = if (isBlank()) 0 else toInt()

private fun String.toDecimalSummands(
    commaChar: String = ".",
    decimalPlacesAfterComma: Int = 2
): List<Double> {
    val factor = 10.0.pow(decimalPlacesAfterComma)
    return this
        .replace(commaChar, ".")
        .split("+")
        .map { it.toDouble().times(factor).toInt().div(factor) }
}

private fun <T> String.convertBlankElse(blankReplacement: T, converter: (String) -> T): T {
    return if (isBlank()) blankReplacement else converter(this)
}

fun convertInputMeterListe(meterListe: String): List<Double> {
    return meterListe.convertBlankElse(listOf()) { it.toDecimalSummands() }
}
