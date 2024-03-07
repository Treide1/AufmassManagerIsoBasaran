package com.example.aufmassmanageriso_basaran.data.excel

import com.example.aufmassmanageriso_basaran.data.remote.bauvorhaben.BauvorhabenDto
import com.example.aufmassmanageriso_basaran.data.utility.toFormattedString

val BAUVORHABEN_EXCEL_HEADER_ROW = listOf(
    "Name",
    "Aufmassnummer",
    "Auftragsnummer",
    "Notiz",
    "Erstellt am",
    "Letzter Export am"
)

fun BauvorhabenDto.toExcelContentRow(): List<String> {
    return listOf(
        name,
        aufmassNummer.toString(),
        auftragsNummer?.toString() ?: "",
        notiz ?: "",
        _zeitstempel?.toFormattedString() ?: "",
        _letzterExportZeitstempel?.toFormattedString() ?: ""
    )
}