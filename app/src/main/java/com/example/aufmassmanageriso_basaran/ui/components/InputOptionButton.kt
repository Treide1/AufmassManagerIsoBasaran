package com.example.aufmassmanageriso_basaran.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

@Composable
fun AddInputButton(
    isAdded: MutableState<Boolean>,
    addOptionLabel: String,
    content: @Composable (requestFocusModifier: Modifier) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    if (!isAdded.value) {
        OutlinedButton(
            onClick = {isAdded.value = true }
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            Text(text = addOptionLabel)
        }
    }
    AnimatedVisibility(
        visible = isAdded.value
    ) {
        content(Modifier.focusRequester(focusRequester))
        LaunchedEffect(key1 = isAdded) {
            if (isAdded.value) focusRequester.requestFocus()
        }
    }
}