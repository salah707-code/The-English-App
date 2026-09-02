package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.Word

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditWordDialog(
    wordToEdit: Word? = null,
    onDismiss: () -> Unit,
    onSave: (Word) -> Unit
) {
    var english by remember { mutableStateOf(wordToEdit?.english ?: "") }
    var arabic by remember { mutableStateOf(wordToEdit?.arabic ?: "") }
    var category by remember { mutableStateOf(wordToEdit?.category ?: "Daily Life") }
    var level by remember { mutableStateOf(wordToEdit?.level ?: "A1") }
    var partOfSpeech by remember { mutableStateOf(wordToEdit?.partOfSpeech ?: "Noun") }
    var pronunciation by remember { mutableStateOf(wordToEdit?.pronunciation ?: "") }
    var example by remember { mutableStateOf(wordToEdit?.example ?: "") }
    var exampleArabic by remember { mutableStateOf(wordToEdit?.exampleArabic ?: "") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var levelExpanded by remember { mutableStateOf(false) }
    var posExpanded by remember { mutableStateOf(false) }

    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (wordToEdit == null) stringResource(R.string.add_word) else stringResource(R.string.edit_word),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = english,
                    onValueChange = {
                        english = it
                        showError = false
                    },
                    label = { Text("English Word *") },
                    isError = showError && english.isBlank(),
                    modifier = Modifier.fillMaxWidth().testTag("input_english"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = arabic,
                    onValueChange = {
                        arabic = it
                        showError = false
                    },
                    label = { Text("Arabic Meaning (المعنى بالعربية) *") },
                    isError = showError && arabic.isBlank(),
                    modifier = Modifier.fillMaxWidth().testTag("input_arabic"),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    // Level Dropdown
                    ExposedDropdownMenuBox(
                        expanded = levelExpanded,
                        onExpandedChange = { levelExpanded = !levelExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = level,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.filter_level)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = levelExpanded) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = levelExpanded,
                            onDismissRequest = { levelExpanded = false }
                        ) {
                            Word.ALL_LEVELS.forEach { lvl ->
                                DropdownMenuItem(
                                    text = { Text(lvl) },
                                    onClick = {
                                        level = lvl
                                        levelExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Part of Speech Dropdown
                    ExposedDropdownMenuBox(
                        expanded = posExpanded,
                        onExpandedChange = { posExpanded = !posExpanded },
                        modifier = Modifier.weight(1.2f)
                    ) {
                        OutlinedTextField(
                            value = partOfSpeech,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.filter_pos)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = posExpanded) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = posExpanded,
                            onDismissRequest = { posExpanded = false }
                        ) {
                            Word.ALL_PARTS_OF_SPEECH.forEach { pos ->
                                DropdownMenuItem(
                                    text = { Text(pos) },
                                    onClick = {
                                        partOfSpeech = pos
                                        posExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.filter_category)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        Word.ALL_CATEGORIES.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = pronunciation,
                    onValueChange = { pronunciation = it },
                    label = { Text("Pronunciation / IPA (/həˈloʊ/)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = example,
                    onValueChange = { example = it },
                    label = { Text(stringResource(R.string.example_sentence)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = exampleArabic,
                    onValueChange = { exampleArabic = it },
                    label = { Text(stringResource(R.string.example_translation)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (english.isBlank() || arabic.isBlank()) {
                        showError = true
                        return@Button
                    }
                    val word = (wordToEdit ?: Word(english = "", arabic = "")).copy(
                        english = english.trim(),
                        arabic = arabic.trim(),
                        category = category,
                        level = level,
                        partOfSpeech = partOfSpeech,
                        pronunciation = pronunciation.trim(),
                        example = example.trim(),
                        exampleArabic = exampleArabic.trim(),
                        updatedAt = System.currentTimeMillis()
                    )
                    onSave(word)
                },
                modifier = Modifier.testTag("btn_dialog_save")
            ) {
                Text(stringResource(R.string.dialog_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}
