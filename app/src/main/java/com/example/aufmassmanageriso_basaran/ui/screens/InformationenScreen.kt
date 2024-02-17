package com.example.aufmassmanageriso_basaran.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme

/**
 * Home screen of the app.
 *
 * Explains the following concepts (in german):
 * * Select Bauvorhaben if not already selected
 * * In case of erroneous Bauvorhaben selection, clear the selection
 * * If the Bauvorhaben is missing, create a new one
 * * The not-in-sync warning indicates that the data might not be up to date
 * * Create Eintrag for normal Aufmaß
 * * Create Spezial for Aufmaß out of the ordinary
 */
@Composable
fun InformationenScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // # Willkommen
        Header(title = "Willkommen im Iso-Basaran Aufmaß-Manager!")

        // ## Bauvorhaben
        Chapter(name = "Bauvorhaben")
        Body("""Bevor du Einträge erstellen kannst, muss ein Bauvorhaben ausgewählt sein.""")
        Body("""
            |Gehe zu "Bauvorhaben auswählen" und wähle ein Bauvorhaben aus.
            |Falls das Bauvorhaben nicht in der Liste ist, erstelle ein Neues unter "Bauvorhaben hinzufügen".
            """.trimMargin())

        // ## Einträge
        Chapter(name = "Einträge")
        Body("""
            |Erstelle einen Eintrag für ein normales Aufmaß unter "Eintrag erstellen".
            |Falls ein spezielles Aufmaß erforderlich ist, gehe stattdessen zu "Spezial-Eintrag erstellen".
            """.trimMargin())

        // ## Internetverbindung
        Chapter(name = "Internetverbindung")
        Body(
            text = buildAnnotatedString {
                append("""Falls das Nicht-Synchronisiert-Symbol """)
                appendInlineContent("not_sync", "[icon]")
                append(""" 
                    |angezeigt wird, ist das Gerät offline.
                """.trimMargin())
            },
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
            )
        )
        Body("""
            |Die meisten Operationen können offline durchgeführt werden. 
            |Wenn das Gerät wieder online ist, werden alle Änderungen synchronisiert.
            """.trimMargin()
        )
        Body(text = buildAnnotatedString {
            appendLine("""Die einzigen Operationen, die nur online funktionieren, sind: """)
            withStyle(style = ParagraphStyle(
                lineHeight = 20.sp
            )) {
                appendLine(""" • "Bauvorhaben auswählen" """)
                // appendLine(""" • "Aufmaß exportieren" """) // TODO: uncomment later
            }
        })
    }
}

@Composable
private fun Header(title: String) {
    Text(title, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
}

@Composable
private fun Chapter(name: String) {
    Text(name, textDecoration = TextDecoration.Underline, textAlign = TextAlign.Center)
}

@Composable
private fun Body(text: String) {
    Text(text, softWrap = true, style = TextStyle.Default.copy(lineBreak = LineBreak.Heading))
}

@Composable
private fun Body(text: AnnotatedString, inlineContent: Map<String, InlineTextContent> = mapOf()) {
    Text(
        text = text,
        inlineContent = inlineContent,
        modifier = Modifier.fillMaxWidth(),
        softWrap = true,
        style = TextStyle.Default.copy(lineBreak = LineBreak.Heading)
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun InformationenScreenPreview() {
    AufmassManagerIsoBasaranTheme {
        InformationenScreen()
    }
}