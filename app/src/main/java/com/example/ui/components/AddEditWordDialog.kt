package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.AudioPlayerManager
import com.example.data.model.CategoryEntity
import com.example.data.model.Word
import com.example.ui.theme.StarGold
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditWordDialog(
    wordToEdit: Word? = null,
    categories: List<CategoryEntity> = emptyList(),
    audioPlayerManager: AudioPlayerManager? = null,
    onDismiss: () -> Unit,
    onSave: (Word) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var english by remember { mutableStateOf(wordToEdit?.english ?: "") }
    var arabic by remember { mutableStateOf(wordToEdit?.arabic ?: "") }
    var category by remember { mutableStateOf(wordToEdit?.category ?: (categories.firstOrNull()?.name ?: "عام")) }
    var newCategoryInput by remember { mutableStateOf("") }
    var isAddingCustomCategory by remember { mutableStateOf(false) }

    var partOfSpeech by remember { mutableStateOf(wordToEdit?.partOfSpeech ?: "Noun") }
    var pronunciation by remember { mutableStateOf(wordToEdit?.pronunciation ?: "") }
    var example by remember { mutableStateOf(wordToEdit?.example ?: "") }
    var exampleArabic by remember { mutableStateOf(wordToEdit?.exampleArabic ?: "") }
    var isFavorite by remember { mutableStateOf(wordToEdit?.isFavorite ?: false) }

    // Real audio file paths/URIs
    var wordAudioPath by remember { mutableStateOf(wordToEdit?.audioUrl ?: "") }
    var sentenceAudioPath by remember { mutableStateOf(wordToEdit?.sentenceAudioUrl ?: "") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var posExpanded by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    val currentlyPlaying by audioPlayerManager?.currentlyPlaying?.collectAsState() ?: remember { mutableStateOf(null) }

    // Launcher for picking Word Audio file
    val pickWordAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && audioPlayerManager != null) {
            scope.launch {
                val res = audioPlayerManager.saveAudioFromUri(uri, prefix = "word_${english.take(6).ifBlank { "audio" }}")
                res.onSuccess { savedPath ->
                    wordAudioPath = savedPath
                    Toast.makeText(context, "تم ربط ملف صوت الكلمة بنجاح", Toast.LENGTH_SHORT).show()
                }.onFailure { err ->
                    Toast.makeText(context, "تعذر حفظ الملف: ${err.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Launcher for picking Sentence Audio file
    val pickSentenceAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && audioPlayerManager != null) {
            scope.launch {
                val res = audioPlayerManager.saveAudioFromUri(uri, prefix = "sentence_${english.take(6).ifBlank { "audio" }}")
                res.onSuccess { savedPath ->
                    sentenceAudioPath = savedPath
                    Toast.makeText(context, "تم ربط ملف صوت الجملة بنجاح", Toast.LENGTH_SHORT).show()
                }.onFailure { err ->
                    Toast.makeText(context, "تعذر حفظ الملف: ${err.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = {
            audioPlayerManager?.stop()
            onDismiss()
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (wordToEdit == null) "إضافة مفردة جديدة" else "تعديل الكلمة",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) StarGold else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // English Word
                OutlinedTextField(
                    value = english,
                    onValueChange = {
                        english = it
                        showError = false
                    },
                    label = { Text("English Word (الكلمة بالإنجليزية) *") },
                    isError = showError && english.isBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_english"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Arabic Meaning
                OutlinedTextField(
                    value = arabic,
                    onValueChange = {
                        arabic = it
                        showError = false
                    },
                    label = { Text("Arabic Meaning (المعنى بالعربية) *") },
                    isError = showError && arabic.isBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_arabic"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Written Pronunciation / IPA
                OutlinedTextField(
                    value = pronunciation,
                    onValueChange = { pronunciation = it },
                    label = { Text("Written Pronunciation / IPA (النطق المكتوب مثال: /əˈbændən/)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Part of Speech Dropdown
                ExposedDropdownMenuBox(
                    expanded = posExpanded,
                    onExpandedChange = { posExpanded = !posExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = partOfSpeech,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Part of Speech (نوع الكلمة)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = posExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
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

                // Category Selection
                if (isAddingCustomCategory) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newCategoryInput,
                            onValueChange = { newCategoryInput = it },
                            label = { Text("اسم التصنيف الجديد") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                if (newCategoryInput.isNotBlank()) {
                                    category = newCategoryInput.trim()
                                }
                                isAddingCustomCategory = false
                            }
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "تأكيد")
                        }
                        IconButton(
                            onClick = { isAddingCustomCategory = false }
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "إلغاء")
                        }
                    }
                } else {
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("التصنيف (Category)") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name) },
                                    onClick = {
                                        category = cat.name
                                        categoryExpanded = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("+ إضافة تصنيف جديد...") },
                                onClick = {
                                    categoryExpanded = false
                                    isAddingCustomCategory = true
                                }
                            )
                        }
                    }
                }

                // Example Sentence
                OutlinedTextField(
                    value = example,
                    onValueChange = { example = it },
                    label = { Text("Example Sentence (جملة المثال بالإنجليزية)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp)
                )

                // Sentence Translation
                OutlinedTextField(
                    value = exampleArabic,
                    onValueChange = { exampleArabic = it },
                    label = { Text("Sentence Translation (ترجمة الجملة للعربية)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                // Real Audio Files Section
                Text(
                    text = "الملفات الصوتية الحقيقية (Real Audio)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                // 1. Word Audio
                AudioFileItemCard(
                    title = "صوت الكلمة (Word Audio)",
                    filePath = wordAudioPath,
                    isPlaying = currentlyPlaying == wordAudioPath && wordAudioPath.isNotBlank(),
                    onPick = { pickWordAudioLauncher.launch("audio/*") },
                    onPlay = {
                        if (wordAudioPath.isNotBlank()) {
                            audioPlayerManager?.play(
                                wordAudioPath,
                                onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                            )
                        } else {
                            Toast.makeText(context, "الصوت غير متوفر", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onStop = { audioPlayerManager?.stop() },
                    onDelete = {
                        audioPlayerManager?.deleteAudioFile(wordAudioPath)
                        wordAudioPath = ""
                    }
                )

                // 2. Sentence Audio
                AudioFileItemCard(
                    title = "صوت الجملة (Sentence Audio)",
                    filePath = sentenceAudioPath,
                    isPlaying = currentlyPlaying == sentenceAudioPath && sentenceAudioPath.isNotBlank(),
                    onPick = { pickSentenceAudioLauncher.launch("audio/*") },
                    onPlay = {
                        if (sentenceAudioPath.isNotBlank()) {
                            audioPlayerManager?.play(
                                sentenceAudioPath,
                                onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                            )
                        } else {
                            Toast.makeText(context, "الصوت غير متوفر للجملة", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onStop = { audioPlayerManager?.stop() },
                    onDelete = {
                        audioPlayerManager?.deleteAudioFile(sentenceAudioPath)
                        sentenceAudioPath = ""
                    }
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
                    audioPlayerManager?.stop()
                    val word = (wordToEdit ?: Word(english = "", arabic = "")).copy(
                        english = english.trim(),
                        arabic = arabic.trim(),
                        category = category.trim().ifBlank { "عام" },
                        level = "",
                        partOfSpeech = partOfSpeech,
                        pronunciation = pronunciation.trim(),
                        example = example.trim(),
                        exampleArabic = exampleArabic.trim(),
                        audioUrl = wordAudioPath,
                        sentenceAudioUrl = sentenceAudioPath,
                        isFavorite = isFavorite,
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
            TextButton(
                onClick = {
                    audioPlayerManager?.stop()
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}

@Composable
private fun AudioFileItemCard(
    title: String,
    filePath: String,
    isPlaying: Boolean,
    onPick: () -> Unit,
    onPlay: () -> Unit,
    onStop: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (filePath.isNotBlank()) {
                        File(filePath).name.ifBlank { "ملف صوتي مربوط" }
                    } else {
                        "الصوت غير متوفر"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (filePath.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (filePath.isNotBlank()) {
                    IconButton(
                        onClick = if (isPlaying) onStop else onPlay,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Stop" else "Play",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Audio",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                OutlinedButton(
                    onClick = onPick,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (filePath.isNotBlank()) "استبدال" else "اختيار ملف",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
