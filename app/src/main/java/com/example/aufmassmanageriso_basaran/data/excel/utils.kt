package com.example.aufmassmanageriso_basaran.data.excel

import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook

fun HSSFWorkbook.createSheetWithPivotTable(
    sheetName: String,
    sheetContent: List<List<String>>
) {

    createSheet(sheetName).apply {
        sheetContent.forEachIndexed { rowIndex, rowContent ->
            val row = createRow(rowIndex)
            rowContent.forEachIndexed { cellIndex, cellContent ->
                row.createCell(cellIndex).setCellValue(cellContent)
            }
        }
    }
}