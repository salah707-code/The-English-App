package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.CategoryEntity
import com.example.data.model.Word
import com.example.ui.components.AddEditWordDialog
import com.example.ui.components.WordCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.StarGold
import com.example.viewmodel.SortOrder
import com.example.viewmodel.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordsScreen(
    viewModel: WordViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    val words by viewModel.filteredWords.collectAsStateWithLifecycle()
    val categories by viewModel.allCategories.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()
    val currentSort by viewModel.sortOrder.collectAsStateWithLifecycle()

    val tableFontSizeKey by viewModel.preferences.tableFontSize.collectAsStateWithLifecycle()
    val viewMode by viewModel.preferences.vocabularyViewMode.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var wordToEdit by remember { mutableStateOf<Word?>(null) }
    var wordToMoveCategory by remember { mutableStateOf<Word?>(null) }
    var wordToDeleteConfirm by remember { mutableStateOf<Word?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showFontSizeMenu by remember { mutableStateOf(false) }

    val tableFontSizeSp = when (tableFontSizeKey) {
        "SMALL" -> 12.sp
        "LARGE" -> 17.sp
        else -> 14.sp
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "مكتبة المفردات",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (words.isEmpty()) "لا توجد كلمات" else "${words.size} كلمة معروضة",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // View Mode Toggle (Table / Cards)
                    IconButton(
                        onClick = {
                            val newMode = if (viewMode == "TABLE") "CARDS" else "TABLE"
                            viewModel.preferences.setVocabularyViewMode(newMode)
                        },
                        modifier = Modifier.testTag("btn_toggle_view_mode")
                    ) {
                        Icon(
                            imageVector = if (viewMode == "TABLE") Icons.Default.GridView else Icons.Default.TableChart,
                            contentDescription = if (viewMode == "TABLE") "عرض البطاقات" else "عرض الجدول"
                        )
                    }

                    // Font Size Selector for Table
                    Box {
                        IconButton(
                            onClick = { showFontSizeMenu = true },
                            modifier = Modifier.testTag("btn_table_font_size")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "حجم الخط"
                            )
                        }

                        DropdownMenu(
                            expanded = showFontSizeMenu,
                            onDismissRequest = { showFontSizeMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("خط صغير (Small)") },
                                onClick = {
                                    viewModel.preferences.setTableFontSize("SMALL")
                                    showFontSizeMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("خط متوسط (Medium)") },
                                onClick = {
                                    viewModel.preferences.setTableFontSize("MEDIUM")
                                    showFontSizeMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("خط كبير (Large)") },
                                onClick = {
                                    viewModel.preferences.setTableFontSize("LARGE")
                                    showFontSizeMenu = false
                                }
                            )
                        }
                    }

                    // Sort Menu
                    Box {
                        IconButton(
                            onClick = { showSortMenu = true },
                            modifier = Modifier.testTag("btn_sort")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = "ترتيب الكلمات"
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.sort_az)) },
                                onClick = {
                                    viewModel.sortOrder.value = SortOrder.AZ
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.sort_za)) },
                                onClick = {
                                    viewModel.sortOrder.value = SortOrder.ZA
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.sort_recent)) },
                                onClick = {
                                    viewModel.sortOrder.value = SortOrder.RECENT
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("حسب موعد المراجعة") },
                                onClick = {
                                    viewModel.sortOrder.value = SortOrder.DUE_DATE
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_word")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_word)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("input_search_words"),
                placeholder = { Text(stringResource(R.string.words_search_hint)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear Search")
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            // Category Filter Chips (Populated dynamically from user's categories)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val isAll = selectedCategory == null && selectedStatus == null
                    FilterChip(
                        selected = isAll,
                        onClick = {
                            viewModel.selectedCategory.value = null
                            viewModel.selectedStatusFilter.value = null
                        },
                        label = { Text("الكل (${words.size})") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                items(categories, key = { it.id }) { cat ->
                    val isSelected = selectedCategory == cat.name
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.selectedCategory.value = if (isSelected) null else cat.name
                        },
                        label = { Text(cat.name) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Main Content: Empty State vs Table View vs Cards View
            if (words.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "لا توجد كلمات حتى الآن",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ابدأ بإضافة مفرداتك الخاصة وملفات الصوت بالضغط على (+) بالأسفل، أو استورد قائمة الكلمات من ملف CSV.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("إضافة كلمة جديدة")
                        }
                    }
                }
            } else if (viewMode == "TABLE") {
                // Table View with Horizontal Scroll, Non-clipping Layout, and Full Actions
                VocabularyTableView(
                    words = words,
                    fontSize = tableFontSizeSp,
                    onWordClick = { word ->
                        viewModel.selectWord(word)
                        onNavigate(Screen.WordDetail.route)
                    },
                    onEditClick = { word -> wordToEdit = word },
                    onDeleteClick = { word -> wordToDeleteConfirm = word },
                    onPlayWordAudio = { word ->
                        viewModel.playWordAudio(
                            word,
                            onNotAvailable = {
                                Toast.makeText(context, "صوت الكلمة غير متوفر", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onPlaySentenceAudio = { word ->
                        viewModel.playSentenceAudio(
                            word,
                            onNotAvailable = {
                                Toast.makeText(context, "صوت الجملة غير متوفر", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onToggleFavorite = { word -> viewModel.toggleFavorite(word) },
                    onMoveCategoryClick = { word -> wordToMoveCategory = word },
                    onCopyClick = { word ->
                        val text = "${word.english} - ${word.arabic}\n${word.example} ${word.exampleArabic}".trim()
                        clipboard.setText(AnnotatedString(text))
                        Toast.makeText(context, "تم نسخ الكلمة والمثال", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                // Cards View
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(words, key = { it.id }) { word ->
                        WordCard(
                            word = word,
                            onClick = {
                                viewModel.selectWord(word)
                                onNavigate(Screen.WordDetail.route)
                            },
                            onAudioClick = { viewModel.playWordAudio(word) },
                            onFavoriteClick = { viewModel.toggleFavorite(word) }
                        )
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showAddDialog || wordToEdit != null) {
        AddEditWordDialog(
            wordToEdit = wordToEdit,
            categories = categories,
            audioPlayerManager = viewModel.audioPlayerManager,
            onDismiss = {
                showAddDialog = false
                wordToEdit = null
            },
            onSave = { savedWord ->
                viewModel.saveWord(savedWord)
                showAddDialog = false
                wordToEdit = null
                Toast.makeText(context, "تم حفظ الكلمة بنجاح", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Move to Category Dialog
    wordToMoveCategory?.let { word ->
        MoveCategoryDialog(
            word = word,
            categories = categories,
            onDismiss = { wordToMoveCategory = null },
            onCategorySelected = { targetCategory ->
                viewModel.saveWord(word.copy(category = targetCategory, updatedAt = System.currentTimeMillis()))
                wordToMoveCategory = null
                Toast.makeText(context, "تم نقل الكلمة إلى $targetCategory", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Confirm Move to Trash Dialog
    wordToDeleteConfirm?.let { word ->
        AlertDialog(
            onDismissRequest = { wordToDeleteConfirm = null },
            title = { Text("نقل إلى سلة المهملات") },
            text = { Text("هل تريد نقل الكلمة «${word.english}» إلى سلة المهملات؟ يمكنك استعادتها لاحقاً.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.moveToTrash(word)
                        wordToDeleteConfirm = null
                        Toast.makeText(context, "تم نقل الكلمة إلى سلة المهملات", Toast.LENGTH_SHORT).show()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("نقل للمهملات")
                }
            },
            dismissButton = {
                TextButton(onClick = { wordToDeleteConfirm = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

/**
 * جدول الكلمات وفق المتطلبات المحددة:
 * الكلمة | المعنى | الجملة والترجمة | النطق | التعديلات
 * التعديلات:
 * - تعديل
 * - حذف / نقل للمهملات
 * - تشغيل صوت الكلمة
 * - تشغيل صوت الجملة
 * - مفضلة
 * - نقل إلى تصنيف
 * - نسخ
 */
@Composable
fun VocabularyTableView(
    words: List<Word>,
    fontSize: androidx.compose.ui.unit.TextUnit,
    onWordClick: (Word) -> Unit,
    onEditClick: (Word) -> Unit,
    onDeleteClick: (Word) -> Unit,
    onPlayWordAudio: (Word) -> Unit,
    onPlaySentenceAudio: (Word) -> Unit,
    onToggleFavorite: (Word) -> Unit,
    onMoveCategoryClick: (Word) -> Unit,
    onCopyClick: (Word) -> Unit
) {
    val horizontalScrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(horizontalScrollState)
        ) {
            // Table Header Row
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                modifier = Modifier.width(880.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الكلمة (Word)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.width(160.dp)
                    )
                    Text(
                        text = "المعنى (Meaning)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.width(160.dp)
                    )
                    Text(
                        text = "الجملة وترجمتها (Sentence)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.width(220.dp)
                    )
                    Text(
                        text = "النطق (IPA)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.width(110.dp)
                    )
                    Text(
                        text = "التعديلات والإجراءات (Actions)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.width(230.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Table Body
            LazyColumn(
                modifier = Modifier
                    .width(880.dp)
                    .fillMaxHeight(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(words, key = { it.id }) { word ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Column 1: Word
                            Column(modifier = Modifier.width(160.dp)) {
                                Text(
                                    text = word.english,
                                    fontSize = (fontSize.value + 2).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (word.category.isNotBlank()) {
                                    Text(
                                        text = word.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // Column 2: Meaning
                            Text(
                                text = word.arabic,
                                fontSize = fontSize,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.width(160.dp)
                            )

                            // Column 3: Sentence & Translation
                            Column(modifier = Modifier.width(220.dp)) {
                                if (word.example.isNotBlank()) {
                                    Text(
                                        text = word.example,
                                        fontSize = fontSize,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                if (word.exampleArabic.isNotBlank()) {
                                    Text(
                                        text = word.exampleArabic,
                                        fontSize = (fontSize.value - 1).sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (word.example.isBlank() && word.exampleArabic.isBlank()) {
                                    Text(
                                        text = "غير متوفر",
                                        fontSize = fontSize,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                }
                            }

                            // Column 4: Pronunciation
                            Text(
                                text = word.pronunciation.ifBlank { "غير متوفر" },
                                fontSize = fontSize,
                                color = if (word.pronunciation.isNotBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.width(110.dp)
                            )

                            // Column 5: Action Buttons
                            Row(
                                modifier = Modifier.width(230.dp),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 1. Word Audio
                                IconButton(
                                    onClick = { onPlayWordAudio(word) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "تشغيل صوت الكلمة",
                                        tint = if (word.audioUrl.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // 2. Sentence Audio
                                IconButton(
                                    onClick = { onPlaySentenceAudio(word) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "تشغيل صوت الجملة",
                                        tint = if (word.sentenceAudioUrl.isNotBlank()) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // 3. Favorite
                                IconButton(
                                    onClick = { onToggleFavorite(word) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (word.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                        contentDescription = "مفضلة",
                                        tint = if (word.isFavorite) StarGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // 4. Edit
                                IconButton(
                                    onClick = { onEditClick(word) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "تعديل",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // 5. Move to Category
                                IconButton(
                                    onClick = { onMoveCategoryClick(word) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DriveFileMove,
                                        contentDescription = "نقل لتصنيف",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // 6. Copy
                                IconButton(
                                    onClick = { onCopyClick(word) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "نسخ",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // 7. Move to Trash
                                IconButton(
                                    onClick = { onDeleteClick(word) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "نقل إلى سلة المهملات",
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
}

@Composable
fun MoveCategoryDialog(
    word: Word,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onCategorySelected: (String) -> Unit
) {
    var newCategory by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("نقل الكلمة إلى تصنيف") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("اختر التصنيف الجديد للكلمة «${word.english}»:")

                categories.forEach { cat ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCategorySelected(cat.name) }
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = if (word.category == cat.name) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = cat.name,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newCategory,
                    onValueChange = { newCategory = it },
                    label = { Text("أو اكتب تصنيفاً جديداً") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newCategory.isNotBlank()) {
                        onCategorySelected(newCategory.trim())
                    }
                },
                enabled = newCategory.isNotBlank()
            ) {
                Text("نقل")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
