package com.example.aufmassmanageriso_basaran.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aufmassmanageriso_basaran.data.local.DerivedFormField
import com.example.aufmassmanageriso_basaran.data.local.EintragForm
import com.example.aufmassmanageriso_basaran.data.local.TextFormField
import com.example.aufmassmanageriso_basaran.ui.components.ButtonRow
import com.example.aufmassmanageriso_basaran.ui.components.FormTextInput
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEintragScreen(
    form: EintragForm,
    coroutineContext: CoroutineContext = CoroutineScope(Dispatchers.Default).coroutineContext,
    createEintrag: (EintragForm) -> List<String> = { emptyList() },
    onAbort: () -> Unit = {}
) {
    val fields by form.fields.collectAsState()
    val context = LocalContext.current

    Scaffold(
        contentWindowInsets = WindowInsets.ime
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            //////////////////////////
            // Populating the form fields
            fields.forEach { field ->
                when (field) {
                    is TextFormField -> {
                        FormTextInput(textFormField = field)
                    }
                    is DerivedFormField -> {
                        val text by field.derivedText.collectAsState("", coroutineContext)

                        OutlinedTextField(
                            value = text,
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            enabled = false,
                            label = {
                                Text(text = field.name)
                            }
                        )
                    }
                }
            }
            //////////////////////////
            ButtonRow {
                // Cancel
                OutlinedButton(
                    onClick = onAbort,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(text = "Abbrechen")
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Create
                OutlinedButton(
                    onClick = {
                        val responses = createEintrag(form)
                        responses.forEach { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text(text = "Erstellen")
                }
            }
            //////////////////////////
        }
    }

}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun AddEntryScreenPreview() {
    AufmassManagerIsoBasaranTheme {
        CreateEintragScreen(EintragForm())
    }
}
