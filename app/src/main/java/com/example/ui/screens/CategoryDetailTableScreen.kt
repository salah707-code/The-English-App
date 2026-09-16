package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.Word
import com.example.ui.components.CategoryIconHelper
import com.example.viewmodel.WordViewModel
import kotlinx.coroutines.launch

/**
 * أنماط العرض المتوفرة لإدارة مفردات التصنيف:
 * 1. TABLE: عرض جدول تفاعلي منظم
 * 2. GRID: عرض شبكي ومربعات تفاعلية
 * 3. LIST: عرض بطاقات تفصيلية
 */
enum class CategoryViewMode(val title: String) {
    TABLE("جدول"),
    GRID("مربعات"),
    LIST("بطاقات")
}

enum class TableSortColumn {
    WORD,        // الكلمة
    MEANING,     // المعنى
    TRANSLATION, // الترجمة
    LEVEL        // المستوى
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailTableScreen(
    category: CategoryEntity,
    viewModel: WordViewModel,
    onNavigateBack: () -> Unit
) {
    val categoryWords by viewModel.getWordsForCategory(category.name).collectAsState(initial = emptyList())
    val categoryColor = CategoryIconHelper.parseColor(category.colorHex)

    var searchQuery by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf<String?>(null) }
    var sortColumn by remember { mutableStateOf(TableSortColumn.WORD) }
    var sortAscending by remember { mutableStateOf(true) }
    var viewMode by remember { mutableStateOf(CategoryViewMode.TABLE) }

    // Dialogs state
    var isAddingWord by remember { mutableStateOf(false) }
    var editingWord by remember { mutableStateOf<Word?>(null) }
    var wordToDelete by remember { mutableStateOf<Word?>(null) }
    var isImporting by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // File picker launcher supporting .xlsx, .csv, and text files
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isImporting = true
            viewModel.importWordsIntoCategory(it, category.name) { imported, updated ->
                isImporting = false
                scope.launch {
                    snackbarHostState.showSnackbar(
                        "اكتمل الاستيراد بنجاح: تم استيراد $imported كلمة جديدة، وتحديث $updated كلمة"
                    )
                }
            }
        }
    }

    // Filter & Sort logic
    val filteredWords by remember(categoryWords, searchQuery, selectedLevel, sortColumn, sortAscending) {
        derivedStateOf {
            categoryWords
                .filter { word ->
                    val matchesQuery = searchQuery.isBlank() ||
                            word.english.contains(searchQuery, ignoreCase = true) ||
                            word.arabic.contains(searchQuery, ignoreCase = true) ||
                            word.subcategory.contains(searchQuery, ignoreCase = true) ||
                            word.example.contains(searchQuery, ignoreCase = true)
                    val matchesLevel = selectedLevel == null || word.level.equals(selectedLevel, ignoreCase = true)
                    matchesQuery && matchesLevel
                }
                .sortedWith { a, b ->
                    val result = when (sortColumn) {
                        TableSortColumn.WORD -> a.english.compareTo(b.english, ignoreCase = true)
                        TableSortColumn.MEANING -> a.subcategory.compareTo(b.subcategory, ignoreCase = true)
                        TableSortColumn.TRANSLATION -> a.arabic.compareTo(b.arabic, ignoreCase = true)
                        TableSortColumn.LEVEL -> a.level.compareTo(b.level, ignoreCase = true)
                    }
                    if (sortAscending) result else -result
                }
        }
    }

    // Force RTL for Arabic UX
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(categoryColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = CategoryIconHelper.getIcon(category.iconName),
                                    contentDescription = null,
                                    tint = categoryColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${categoryWords.size} مفردة مسجلة",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع للتصنيفات"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                // Top Action Bar: Import Excel Button & Add Word Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Excel Import Button
                    Button(
                        onClick = {
                            if (!isImporting) {
                                filePickerLauncher.launch("*/*")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_import_excel_csv"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = categoryColor
                        )
                    ) {
                        if (isImporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("جاري الاستيراد...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.FileUpload,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "استيراد Excel / CSV",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Add Word Button
                    OutlinedButton(
                        onClick = { isAddingWord = true },
                        modifier = Modifier
                            .weight(0.85f)
                            .height(48.dp)
                            .testTag("btn_new_word_dialog"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = categoryColor
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, categoryColor)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "إضافة كلمة",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // View Modes Switcher: جدول - مربعات - بطاقات
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "نمط العرض:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Table View Option
                            ViewModeChip(
                                title = "جدول",
                                icon = Icons.Default.TableChart,
                                isSelected = viewMode == CategoryViewMode.TABLE,
                                selectedColor = categoryColor,
                                onClick = { viewMode = CategoryViewMode.TABLE }
                            )

                            // Grid / Squares View Option
                            ViewModeChip(
                                title = "مربعات",
                                icon = Icons.Default.GridView,
                                isSelected = viewMode == CategoryViewMode.GRID,
                                selectedColor = categoryColor,
                                onClick = { viewMode = CategoryViewMode.GRID }
                            )

                            // List View Option
                            ViewModeChip(
                                title = "بطاقات",
                                icon = Icons.Default.ViewAgenda,
                                isSelected = viewMode == CategoryViewMode.LIST,
                                selectedColor = categoryColor,
                                onClick = { viewMode = CategoryViewMode.LIST }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_category_words_input"),
                    placeholder = { Text("بحث في الكلمة، المعنى، الترجمة، أو الجملة...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "بحث"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "مسح")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Level Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = selectedLevel == null,
                        onClick = { selectedLevel = null },
                        label = { Text("الكل (${categoryWords.size})") }
                    )
                    listOf("A1", "A2", "B1", "B2", "C1", "C2").forEach { lvl ->
                        val count = categoryWords.count { it.level.equals(lvl, ignoreCase = true) }
                        FilterChip(
                            selected = selectedLevel.equals(lvl, ignoreCase = true),
                            onClick = {
                                selectedLevel = if (selectedLevel == lvl) null else lvl
                            },
                            label = { Text("$lvl ($count)") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Fields summary indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الحقول: الكلمة • المعنى • الترجمة • الجملة • الصوت",
                        style = MaterialTheme.typography.labelSmall,
                        color = categoryColor,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "النتائج: ${filteredWords.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // View Modes Content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (viewMode) {
                        CategoryViewMode.TABLE -> {
                            CategoryTableView(
                                words = filteredWords,
                                categoryColor = categoryColor,
                                sortColumn = sortColumn,
                                sortAscending = sortAscending,
                                onSortChange = { col ->
                                    if (sortColumn == col) {
                                        sortAscending = !sortAscending
                                    } else {
                                        sortColumn = col
                                        sortAscending = true
                                    }
                                },
                                onSpeak = { viewModel.ttsManager.speak(it.english) },
                                onEdit = { editingWord = it },
                                onDelete = { wordToDelete = it },
                                onAddWord = { isAddingWord = true }
                            )
                        }

                        CategoryViewMode.GRID -> {
                            CategoryGridView(
                                words = filteredWords,
                                categoryColor = categoryColor,
                                onSpeak = { viewModel.ttsManager.speak(it.english) },
                                onEdit = { editingWord = it },
                                onDelete = { wordToDelete = it },
                                onAddWord = { isAddingWord = true }
                            )
                        }

                        CategoryViewMode.LIST -> {
                            CategoryListView(
                                words = filteredWords,
                                categoryColor = categoryColor,
                                onSpeak = { viewModel.ttsManager.speak(it.english) },
                                onEdit = { editingWord = it },
                                onDelete = { wordToDelete = it },
                                onAddWord = { isAddingWord = true }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Add Word Dialog
        if (isAddingWord) {
            WordAddEditDialog(
                title = "إضافة مفردة جديدة إلى: ${category.name}",
                initialEnglish = "",
                initialMeaning = "",
                initialArabic = "",
                initialLevel = "A1",
                initialPos = "Noun",
                initialExample = "",
                initialExampleAr = "",
                categoryColor = categoryColor,
                onDismiss = { isAddingWord = false },
                onConfirm = { en, meaning, ar, lvl, pos, ex, exAr ->
                    viewModel.addWordToCategory(
                        category = category.name,
                        english = en,
                        arabic = ar,
                        meaning = meaning,
                        level = lvl,
                        pos = pos,
                        example = ex,
                        exampleAr = exAr
                    )
                    isAddingWord = false
                    scope.launch {
                        snackbarHostState.showSnackbar("تمت إضافة الكلمة بنجاح")
                    }
                }
            )
        }

        // Inline Edit Word Dialog
        editingWord?.let { word ->
            WordAddEditDialog(
                title = "تعديل الكلمة: ${word.english}",
                initialEnglish = word.english,
                initialMeaning = word.subcategory,
                initialArabic = word.arabic,
                initialLevel = word.level,
                initialPos = word.partOfSpeech,
                initialExample = word.example,
                initialExampleAr = word.exampleArabic,
                categoryColor = categoryColor,
                onDismiss = { editingWord = null },
                onConfirm = { en, meaning, ar, lvl, pos, ex, exAr ->
                    val updated = word.copy(
                        english = en,
                        subcategory = meaning,
                        arabic = ar,
                        level = lvl,
                        partOfSpeech = pos,
                        example = ex,
                        exampleArabic = exAr
                    )
                    viewModel.updateWordInline(updated)
                    editingWord = null
                    scope.launch {
                        snackbarHostState.showSnackbar("تم حفظ التعديلات بنجاح")
                    }
                }
            )
        }

        // Delete Confirmation Dialog
        wordToDelete?.let { word ->
            AlertDialog(
                onDismissRequest = { wordToDelete = null },
                title = { Text("تأكيد الحذف", fontWeight = FontWeight.Bold) },
                text = {
                    Text("هل أنت متأكد من حذف الكلمة '${word.english}' (${word.arabic}) نهائياً؟")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteWordPermanently(word.id)
                            wordToDelete = null
                            scope.launch {
                                snackbarHostState.showSnackbar("تم حذف الكلمة")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("نعم، احذف")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { wordToDelete = null }) {
                        Text("إلغاء")
                    }
                }
            )
        }
    }
}

@Composable
private fun ViewModeChip(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(9.dp),
        color = if (isSelected) selectedColor else Color.Transparent,
        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

/**
 * النمط 1: عرض الجدول التفاعلي (Table View)
 * يحتوي على حقول: الكلمة، المعنى، الترجمة، الجملة، الصوت، والإجراءات
 */
@Composable
fun CategoryTableView(
    words: List<Word>,
    categoryColor: Color,
    sortColumn: TableSortColumn,
    sortAscending: Boolean,
    onSortChange: (TableSortColumn) -> Unit,
    onSpeak: (Word) -> Unit,
    onEdit: (Word) -> Unit,
    onDelete: (Word) -> Unit,
    onAddWord: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    ) {
        val horizontalScrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(horizontalScrollState)
        ) {
            // Table Header Row with 5 requested fields + actions
            Row(
                modifier = Modifier
                    .width(820.dp)
                    .background(categoryColor.copy(alpha = 0.12f))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. الكلمة (Word)
                TableHeaderCell(
                    title = "الكلمة (Word)",
                    width = 160.dp,
                    isSorted = sortColumn == TableSortColumn.WORD,
                    isAscending = sortAscending,
                    onClick = { onSortChange(TableSortColumn.WORD) }
                )

                // 2. المعنى (Meaning)
                TableHeaderCell(
                    title = "المعنى (Meaning)",
                    width = 150.dp,
                    isSorted = sortColumn == TableSortColumn.MEANING,
                    isAscending = sortAscending,
                    onClick = { onSortChange(TableSortColumn.MEANING) }
                )

                // 3. الترجمة (Translation)
                TableHeaderCell(
                    title = "الترجمة (Translation)",
                    width = 150.dp,
                    isSorted = sortColumn == TableSortColumn.TRANSLATION,
                    isAscending = sortAscending,
                    onClick = { onSortChange(TableSortColumn.TRANSLATION) }
                )

                // 4. الجملة (Sentence)
                Text(
                    text = "الجملة (Sentence)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.width(180.dp)
                )

                // 5. الصوت (Audio)
                Text(
                    text = "الصوت",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.width(70.dp),
                    textAlign = TextAlign.Center
                )

                // 6. الإجراءات (Actions)
                Text(
                    text = "إجراءات",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.width(90.dp),
                    textAlign = TextAlign.Center
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Table Rows
            if (words.isEmpty()) {
                Box(
                    modifier = Modifier
                        .width(820.dp)
                        .height(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "لا توجد كلمات في هذا التصنيف تطابق البحث",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onAddWord,
                            colors = ButtonDefaults.buttonColors(containerColor = categoryColor)
                        ) {
                            Text("+ إضافة أول مفردة")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .width(820.dp)
                        .fillMaxSize()
                ) {
                    itemsIndexed(words, key = { _, item -> item.id }) { index, word ->
                        val rowBg = if (index % 2 == 1) {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }

                        TableRowItem(
                            word = word,
                            backgroundColor = rowBg,
                            categoryColor = categoryColor,
                            onSpeak = { onSpeak(word) },
                            onEdit = { onEdit(word) },
                            onDelete = { onDelete(word) }
                        )
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TableRowItem(
    word: Word,
    backgroundColor: Color,
    categoryColor: Color,
    onSpeak: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .width(820.dp)
            .background(backgroundColor)
            .clickable(onClick = onEdit)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. الكلمة (Word)
        Row(
            modifier = Modifier.width(160.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = word.english,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = categoryColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = word.level,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = categoryColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // 2. المعنى (Meaning)
        Text(
            text = word.subcategory.ifBlank { "—" },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(150.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // 3. الترجمة (Translation)
        Text(
            text = word.arabic,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(150.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // 4. الجملة (Sentence)
        Text(
            text = word.example.ifBlank { "—" },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(180.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // 5. الصوت (Audio button with TTS)
        Box(
            modifier = Modifier.width(70.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onSpeak,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "استماع للصوت",
                    tint = categoryColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 6. الإجراءات (Edit & Delete)
        Row(
            modifier = Modifier.width(90.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "تعديل",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "حذف",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * النمط 2: عرض شبكي ومربعات (Square Cards Grid View)
 * يعرض كل كلمة في بطاقة مربعة مستقلة أنيقة تضم الحقول الخمسة: الكلمة، المعنى، الترجمة، الجملة، والصوت
 */
@Composable
fun CategoryGridView(
    words: List<Word>,
    categoryColor: Color,
    onSpeak: (Word) -> Unit,
    onEdit: (Word) -> Unit,
    onDelete: (Word) -> Unit,
    onAddWord: () -> Unit
) {
    if (words.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "لا توجد كلمات مطابقة للعرض الشبكي",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onAddWord,
                    colors = ButtonDefaults.buttonColors(containerColor = categoryColor)
                ) {
                    Text("+ إضافة كلمة جديدة")
                }
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 165.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(words, key = { it.id }) { word ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEdit(word) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        categoryColor.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        // Header: English Word + Level + Sound Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = categoryColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = word.level,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = categoryColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            // زر الصوت 🔊
                            IconButton(
                                onClick = { onSpeak(word) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(categoryColor.copy(alpha = 0.12f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "استماع للصوت",
                                    tint = categoryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // الكلمة (Word)
                        Text(
                            text = word.english,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // الترجمة (Translation)
                        Text(
                            text = word.arabic,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = categoryColor
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // المعنى (Meaning) إن وجد
                        if (word.subcategory.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "المعنى: ${word.subcategory}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // الجملة (Sentence)
                        if (word.example.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "\"${word.example}\"",
                                    style = MaterialTheme.typography.labelSmall.copy(fontStyle = FontStyle.Italic),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Action Buttons: Edit & Delete
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { onEdit(word) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "تعديل",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = { onDelete(word) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * النمط 3: عرض البطاقات التفصيلية (List / Cards View)
 * بطاقات أفقية رحبة تسرد تفاصيل الكلمة والمعنى والترجمة والجملة والصوت بأناقة
 */
@Composable
fun CategoryListView(
    words: List<Word>,
    categoryColor: Color,
    onSpeak: (Word) -> Unit,
    onEdit: (Word) -> Unit,
    onDelete: (Word) -> Unit,
    onAddWord: () -> Unit
) {
    if (words.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "لا توجد كلمات مطابقة لعرض البطاقات",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onAddWord,
                    colors = ButtonDefaults.buttonColors(containerColor = categoryColor)
                ) {
                    Text("+ إضافة مفردة جديدة")
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(words, key = { it.id }) { word ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEdit(word) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // شريط ملون جانبي
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(56.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(categoryColor)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            // Row 1: English Word + Audio Button + Level
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = word.english,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )

                                // زر الصوت 🔊
                                IconButton(
                                    onClick = { onSpeak(word) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "الصوت",
                                        tint = categoryColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = categoryColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = word.level,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = categoryColor,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Row 2: الترجمة + المعنى
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "الترجمة: ${word.arabic}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )

                                if (word.subcategory.isNotBlank()) {
                                    Text(
                                        text = "• المعنى: ${word.subcategory}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Row 3: الجملة التوضيحية
                            if (word.example.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "الجملة: ${word.example}" + if (word.exampleArabic.isNotBlank()) " (${word.exampleArabic})" else "",
                                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Right actions: Edit and Delete
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { onEdit(word) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "تعديل",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = { onDelete(word) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableHeaderCell(
    title: String,
    width: androidx.compose.ui.unit.Dp,
    isSorted: Boolean,
    isAscending: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .width(width)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = if (isSorted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        if (isSorted) {
            Icon(
                imageVector = if (isAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * حوار إضافة وتعديل الكلمة:
 * يتضمن الحقول الصريحة: الكلمة، المعنى، الترجمة، الجملة، المستوى، والنوع
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordAddEditDialog(
    title: String,
    initialEnglish: String,
    initialMeaning: String,
    initialArabic: String,
    initialLevel: String,
    initialPos: String,
    initialExample: String,
    initialExampleAr: String,
    categoryColor: Color,
    onDismiss: () -> Unit,
    onConfirm: (english: String, meaning: String, arabic: String, level: String, pos: String, example: String, exampleAr: String) -> Unit
) {
    var english by remember { mutableStateOf(initialEnglish) }
    var meaning by remember { mutableStateOf(initialMeaning) }
    var arabic by remember { mutableStateOf(initialArabic) }
    var level by remember { mutableStateOf(initialLevel) }
    var pos by remember { mutableStateOf(initialPos) }
    var example by remember { mutableStateOf(initialExample) }
    var exampleAr by remember { mutableStateOf(initialExampleAr) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // الكلمة (Word)
                    OutlinedTextField(
                        value = english,
                        onValueChange = { english = it },
                        label = { Text("الكلمة بالإنجليزية (Word)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_word_english"),
                        singleLine = true
                    )

                    // الترجمة (Translation)
                    OutlinedTextField(
                        value = arabic,
                        onValueChange = { arabic = it },
                        label = { Text("الترجمة بالعربية (Translation)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_word_arabic"),
                        singleLine = true
                    )

                    // المعنى (Meaning)
                    OutlinedTextField(
                        value = meaning,
                        onValueChange = { meaning = it },
                        label = { Text("المعنى / الشرح (Meaning)") },
                        placeholder = { Text("توضيح أو سياق الكلمة...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_word_meaning"),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = level,
                            onValueChange = { level = it },
                            label = { Text("المستوى (A1-C2)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = pos,
                            onValueChange = { pos = it },
                            label = { Text("النوع (Noun/Verb)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // الجملة (Sentence)
                    OutlinedTextField(
                        value = example,
                        onValueChange = { example = it },
                        label = { Text("الجملة بالإنجليزية (Sentence / Example)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    // ترجمة الجملة
                    OutlinedTextField(
                        value = exampleAr,
                        onValueChange = { exampleAr = it },
                        label = { Text("ترجمة الجملة بالعربية (اختياري)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (english.isNotBlank() && arabic.isNotBlank()) {
                            onConfirm(
                                english.trim(),
                                meaning.trim(),
                                arabic.trim(),
                                level.trim().ifBlank { "A1" },
                                pos.trim().ifBlank { "Noun" },
                                example.trim(),
                                exampleAr.trim()
                            )
                        }
                    },
                    enabled = english.isNotBlank() && arabic.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = categoryColor),
                    modifier = Modifier.testTag("btn_save_word_dialog")
                ) {
                    Text("حفظ المفردة", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("إلغاء")
                }
            }
        )
    }
}
