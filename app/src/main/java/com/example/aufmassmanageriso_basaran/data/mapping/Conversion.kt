package com.example.aufmassmanageriso_basaran.data.mapping

import kotlin.math.pow

fun String.toIntBlankAsZero() = if (isBlank()) 0 else toInt()

const val DECIMAL_SEPARATOR = ","

private fun String.toDecimalSummands(
    decimalPlacesAfterComma: Int = 2
): List<Double> {
    val factor = 10.0.pow(decimalPlacesAfterComma)
    return this
        .replace(DECIMAL_SEPARATOR, ".")
        .split("+")
        .map { it.toDouble().times(factor).toInt().div(factor) }
}

private fun <T> String.convertBlankElse(blankReplacement: T, converter: (String) -> T): T {
    return if (isBlank()) blankReplacement else converter(this)
}

fun convertInputMeterListe(meterListe: String): List<Double> {
    return meterListe.convertBlankElse(listOf()) { it.toDecimalSummands() }
}

fun List<Double>.roundedSum(decimalPlacesAfterComma: Int = 2): Double {
    val factor = 10.0.pow(decimalPlacesAfterComma)
    return this.sumOf { it.times(factor).toInt() }.div(factor)
}

fun Double.toPrettyString(): String {
    return this.toString().replace(".", DECIMAL_SEPARATOR)
}
