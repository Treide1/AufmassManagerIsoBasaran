package com.example.aufmassmanageriso_basaran.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Home screen of the app.
 *
 * Explains the following concepts (in german):
 * * Select Bauvorhaben if not already selected
 * * In case of erroneous Bauvorhaben selection, clear the selection
 * * If the Bauvorhaben is missing, create a new one
 * * The not-in-sync warning inidcates that the data might not be up to date
 * * Create Eintrag for normal Aufmaß
 * * Create Spezial for Aufmaß out of the ordinary
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Willkommen im IsoBasaran Aufmaß-Manager!", fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
        Text(text = "Bauvorhaben", textDecoration = TextDecoration.Underline, textAlign = TextAlign.Center)
        Text(text = """
Bevor du Einträge erstellen kannst, muss ein Bauvorhaben ausgewählt sein. Gehe zu "Bauvorhaben auswählen" und wähle ein Bauvorhaben aus. Falls das Bauvorhaben nicht in der Liste ist, erstelle ein Neues unter "Bauvorhaben hinzufügen".
""".trimMargin(),
            softWrap = true,
            style = TextStyle.Default.copy(lineBreak = LineBreak.Heading)
        )
        Text(
            text = buildAnnotatedString {
                append("Falls das Nicht-Synchronisiert-Symbol ")
                appendInlineContent("not_sync", "[icon]")
                append(" angezeigt wird, könnten die Bauvorhaben seit der letzten Synchronisierung veraltet sein. ")
                append("""Stelle eine Internetverbindung her und gehe erneut auf "Bauvorhaben hinzufügen".""")
            },
            modifier = Modifier.wrapContentWidth(),
            inlineContent = mapOf(
                "not_sync" to InlineTextContent(
                    Placeholder(
                        width = 16.sp,
                        height = 16.sp,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline
                    )
                ) {
                    Icon(Icons.Filled.SyncProblem,"")
                }
            ),
            softWrap = true,
            style = TextStyle.Default.copy(lineBreak = LineBreak.Heading)
        )
        Text(text = "Einträge", textDecoration = TextDecoration.Underline, textAlign = TextAlign.Center)
        Text(
            text = """
Erstelle einen Eintrag für ein normales Aufmaß unter "Eintrag erstellen". Falls ein spezielles Aufmaß erforderlich ist, gehe stattdessen zu "Spezial-Eintrag erstellen".
""".trimMargin(),
            softWrap = true,
            style = TextStyle.Default.copy(lineBreak = LineBreak.Heading)
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}