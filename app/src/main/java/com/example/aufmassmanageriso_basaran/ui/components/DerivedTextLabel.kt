package com.example.aufmassmanageriso_basaran.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aufmassmanageriso_basaran.data.local.DerivedFormField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DerivedTextLabel(
    derivedFormField: DerivedFormField
) {
    val text by derivedFormField.derivedText.collectAsState("")

    OutlinedTextField(
        value = text,
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        enabled = false,
        label = {
            Text(text = derivedFormField.name)
        }
    )
}