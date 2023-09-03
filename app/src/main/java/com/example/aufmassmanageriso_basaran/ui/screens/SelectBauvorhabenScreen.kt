package com.example.aufmassmanageriso_basaran.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aufmassmanageriso_basaran.data.remote.BauvorhabenDto
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectBauvorhabenScreen(
    // Search
    searchText: String = "",
    searchResults: List<BauvorhabenDto> = emptyList(),
    isSearching: Boolean = false,
    onSearchTextChange: (String) -> Unit = {},
    // Selection
    selectedBauvorhaben: BauvorhabenDto? = null,
    selectBauvorhaben: (BauvorhabenDto) -> Unit = {}
) {

    // Selection mask
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        var isSearchDisplayed by remember { mutableStateOf(false) }

        // Search results
        ExposedDropdownMenuBox(
            expanded = isSearchDisplayed,
            onExpandedChange = { isSearchDisplayed = !isSearchDisplayed }
        ) {
            TextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                placeholder = { Text(text = "Suche") }
            )
            ExposedDropdownMenu(
                expanded = isSearchDisplayed,
                onDismissRequest = {
                    // TODO: Find good compromise (dismiss includes typing+clicking outside+typing "cancel")
                    //isSearchDisplayed = false
                }
            ) {
                if (isSearching) {
                    // TODO: Fix broken progress indicator (displays 1st frame only)
                    DropdownMenuItem(
                        text = { LinearProgressIndicator(modifier = Modifier.height(24.dp)) },
                        onClick = {}
                    )
                } else {
                    searchResults.forEach { bauvorhabenDto ->
                        DropdownMenuItem(
                            text = { Text(text = bauvorhabenDto.bauvorhaben) },
                            onClick = {
                                println("Selected Bauvorhaben: $bauvorhabenDto")
                                selectBauvorhaben(bauvorhabenDto)
                                isSearchDisplayed = false
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
                        "Bauvorhaben" to selectedBauvorhaben.bauvorhaben,
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
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun SelectBauvorhabenScreenPreview() {
    AufmassManagerIsoBasaranTheme {
        SelectBauvorhabenScreen(
            selectedBauvorhaben = BauvorhabenDto(
                bauvorhaben = "Bauvorhaben 1",
                aufmassNummer = 1,
                auftragsNummer = null,
                notiz = "Notiz"
            )
        )
    }
}