package com.example.aufmassmanageriso_basaran.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aufmassmanageriso_basaran.ui.state.MainViewModel
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme
import com.google.api.Distribution.BucketOptions.Linear

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectBauvorhabenScreen(
    model: MainViewModel = MainViewModel(),
) {
    val searchText by model.searchText.collectAsState()
    val searchResults by model.searchResults.collectAsState()
    val isSearching by model.isSearching.collectAsState()

    // Fetch data when opening screen
    LaunchedEffect(Unit) {
        model.fetchBauvorhaben()
    }

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
                onValueChange = model::onSearchTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                placeholder = { Text(text = "Suche") }
            )
            ExposedDropdownMenu(
                expanded = isSearchDisplayed,
                onDismissRequest = {
                    //isSearchDisplayed = false
                }
            ) {
                if (isSearching) {
                    DropdownMenuItem(
                        text = { LinearProgressIndicator(modifier = Modifier.height(24.dp)) },
                        onClick = {}
                    )
                } else {
                    searchResults.forEach { bauvorhabenDto ->
                        DropdownMenuItem(
                            text = { Text(text = bauvorhabenDto.bauvorhaben) },
                            onClick = {
                                println("Selected Bauvorhaben: ${bauvorhabenDto.bauvorhaben}")
                                model.selectBauvorhaben(bauvorhabenDto)
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

        val selected by model.selectedBauvorhaben.collectAsState()

        if (selected == null) {
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
                        "Bauvorhaben" to selected?.bauvorhaben,
                        "Aufmass-Nummer" to selected?.aufmassNummer?.toString(),
                        "Auftrags-Nummer" to selected?.auftragsNummer?.toString(),
                        "Notiz" to selected?.notiz
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
        SelectBauvorhabenScreen()
    }
}