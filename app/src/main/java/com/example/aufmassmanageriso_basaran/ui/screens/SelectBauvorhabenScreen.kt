package com.example.aufmassmanageriso_basaran.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aufmassmanageriso_basaran.data.remote.bauvorhaben.BauvorhabenDto
import com.example.aufmassmanageriso_basaran.ui.components.ButtonRow
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme

/**
 * Screen for selecting a [BauvorhabenDto] based on a search query.
 * The search is performed on the `name` attribute of the [BauvorhabenDto].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectBauvorhabenScreen(
    // Search
    searchText: String = "",
    searchResults: List<String> = emptyList(),
    isSearching: Boolean = false,
    onSearchTextChange: (text: String) -> Unit = {},
    // Selection
    selectedBauvorhaben: BauvorhabenDto? = null,
    selectBauvorhabenByName: (name: String) -> Unit = {}
) {

    // Selection mask
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        var isSearchDisplayed by remember { mutableStateOf(false) }
        val focusRequester = remember { FocusRequester() }

        // Search results
        ExposedDropdownMenuBox(
            expanded = isSearchDisplayed,
            onExpandedChange = {
                if (isSearchDisplayed) focusRequester.requestFocus()
            }
        ) {
            TextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .focusRequester(focusRequester),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        modifier = Modifier.clickable { isSearchDisplayed = true },
                        contentDescription = null
                    )
                },
                placeholder = { Text(text = "Suche") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    isSearchDisplayed = true
                    focusRequester.captureFocus()
                })
            )
            ExposedDropdownMenu(
                expanded = isSearchDisplayed,
                onDismissRequest = {
                    isSearchDisplayed = false
                    focusRequester.freeFocus()
                }
            ) {
                if (isSearching) {
                    DropdownMenuItem(
                        text = { LinearProgressIndicator(modifier = Modifier.height(24.dp)) },
                        onClick = {}
                    )
                } else {
                    searchResults.forEach { bauvorhabenName ->
                        DropdownMenuItem(
                            text = { Text(text = bauvorhabenName) },
                            onClick = {
                                selectBauvorhabenByName(bauvorhabenName)
                                isSearchDisplayed = false
                                focusRequester.freeFocus()
                            }
                        )
                    }
                    if (searchResults.isEmpty()) {
                        DropdownMenuItem(
                            text = {
                                Text(text = "Keine Ergebnisse", fontStyle = FontStyle.Italic)
                            },
                            onClick = {}
                        )
                    }
                }
            }
        }

        // Display selected bauvorhaben as card
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Ausgewähltes Bauvorhaben", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))

        if (selectedBauvorhaben == null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Kein Bauvorhaben ausgewählt",
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp)
                ) {
                    val bauvorhabenMap = mapOf(
                        "Bauvorhaben" to selectedBauvorhaben.name,
                        "Aufmass-Nummer" to selectedBauvorhaben.aufmassNummer.toString(),
                        "Auftrags-Nummer" to selectedBauvorhaben.auftragsNummer?.toString(),
                        "Notiz" to selectedBauvorhaben.notiz
                    )
                    bauvorhabenMap.forEach { (key, value) ->
                        Text(text = key, fontWeight = FontWeight.SemiBold)
                        Text(text = value ?: "", fontWeight = FontWeight.Light, softWrap = true)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
        ButtonRow {
            OutlinedButton(onClick = { /*TODO*/ }) {
                Text(text = "Auswahl aufheben")
            }
        }

    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun SelectBauvorhabenScreenPreview() {
    AufmassManagerIsoBasaranTheme {
        SelectBauvorhabenScreen(
            selectedBauvorhaben = BauvorhabenDto(
                name = "Bauvorhaben 1",
                aufmassNummer = 1,
                auftragsNummer = null,
                notiz = "Notiz"
            )
        )
    }
}