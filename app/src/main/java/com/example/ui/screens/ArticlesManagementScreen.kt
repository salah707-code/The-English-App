package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Article
import com.example.viewmodel.WordViewModel
import kotlinx.coroutines.launch

private enum class ArticleSortMode(val label: String) {
    MANUAL("ترتيب مخصص"),
    NEWEST("الأحدث أولاً"),
    OLDEST("الأقدم أولاً"),
    LEVEL("حسب المستوى"),
    ALPHABETICAL("أبجدياً (A-Z)")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ArticlesManagementScreen(
    viewModel: WordViewModel,
    onNavigateBack: () -> Unit
) {
    val articles by viewModel.allArticles.collectAsState()
    val userMsg by viewModel.userMessage.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Search and filters
    var searchQuery by remember { mutableStateOf("") }
    var selectedLevelFilter by remember { mutableStateOf("الكل") }
    var sortMode by remember { mutableStateOf(ArticleSortMode.MANUAL) }

    // Dialog states
    var articleToEdit by remember { mutableStateOf<Article?>(null) }
    var isAddArticleOpen by remember { mutableStateOf(false) }
    var articleToRead by remember { mutableStateOf<Article?>(null) }
    var articleToDelete by remember { mutableStateOf<Article?>(null) }
    var showTemplateInfoDialog by remember { mutableStateOf(false) }

    // Excel / CSV File Picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.importArticlesFromFile(uri) { importedCount ->
                scope.launch {
                    snackbarHostState.showSnackbar("تم استيراد $importedCount مقال بنجاح")
                }
            }
        }
    }

    val levels = listOf("الكل", "A1", "A2", "B1", "B2", "C1", "C2")

    // Filter and sort list
    val filteredArticles = remember(articles, searchQuery, selectedLevelFilter, sortMode) {
        var list = articles.filter { article ->
            val matchLevel = selectedLevelFilter == "الكل" || article.level.equals(selectedLevelFilter, ignoreCase = true)
            val matchQuery = searchQuery.isBlank() ||
                    article.title.contains(searchQuery, ignoreCase = true) ||
                    article.content.contains(searchQuery, ignoreCase = true) ||
                    article.keywords.contains(searchQuery, ignoreCase = true) ||
                    article.category.contains(searchQuery, ignoreCase = true)
            matchLevel && matchQuery
        }

        when (sortMode) {
            ArticleSortMode.MANUAL -> list.sortedBy { it.orderIndex }
            ArticleSortMode.NEWEST -> list.sortedByDescending { it.createdAt }
            ArticleSortMode.OLDEST -> list.sortedBy { it.createdAt }
            ArticleSortMode.LEVEL -> list.sortedBy { it.level }
            ArticleSortMode.ALPHABETICAL -> list.sortedBy { it.title.lowercase() }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = null,
                                    tint = Color(0xFF4338CA),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "المقالات والنصوص التعليمية",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Text(
                                text = "${articles.size} مقال مسجل • دعم كامل للإكسل وإعادة الترتيب",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع"
                            )
                        }
                    },
                    actions = {
                        // Excel / CSV Import button
                        IconButton(
                            onClick = {
                                filePickerLauncher.launch(arrayOf(
                                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                    "text/csv",
                                    "text/comma-separated-values",
                                    "text/tab-separated-values",
                                    "text/plain",
                                    "application/vnd.ms-excel",
                                    "*/*"
                                ))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileUpload,
                                contentDescription = "استيراد من إكسل",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Help / Template dialog button
                        IconButton(onClick = { showTemplateInfoDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = "صيغة الملف"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { isAddArticleOpen = true },
                    containerColor = Color(0xFF4338CA),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مقال جديد", fontWeight = FontWeight.Bold)
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Search & Filter header
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        // Search bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("بحث في العنوان، النص، أو الكلمات المفتاحية...") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "مسح")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick actions bar: Level Filter Chips & Sort dropdown
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Sort Dropdown
                            var isSortMenuExpanded by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(
                                    onClick = { isSortMenuExpanded = true },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sort,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = sortMode.label,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }

                                DropdownMenu(
                                    expanded = isSortMenuExpanded,
                                    onDismissRequest = { isSortMenuExpanded = false }
                                ) {
                                    ArticleSortMode.values().forEach { mode ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = mode.label,
                                                    fontWeight = if (mode == sortMode) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            onClick = {
                                                sortMode = mode
                                                isSortMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Import Excel button in bar
                            Button(
                                onClick = {
                                    filePickerLauncher.launch(arrayOf(
                                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                        "text/csv",
                                        "text/comma-separated-values",
                                        "*/*"
                                    ))
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FileUpload,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "استيراد إكسل",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Level Filter Chips
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            levels.forEach { lvl ->
                                val selected = selectedLevelFilter == lvl
                                FilterChip(
                                    selected = selected,
                                    onClick = { selectedLevelFilter = lvl },
                                    label = { Text(lvl, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }
                    }
                }

                // Articles list
                if (filteredArticles.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (searchQuery.isNotBlank() || selectedLevelFilter != "الكل")
                                    "لا توجد مقالات تطابق شروط البحث"
                                else
                                    "لم يتم تسجيل أي مقال حتى الآن",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "يمكنك إضافة مقال جديد يدوياً أو رفع ملف إكسل دفعة واحدة.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = { isAddArticleOpen = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4338CA))
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("إضافة مقال")
                                }
                                OutlinedButton(
                                    onClick = {
                                        filePickerLauncher.launch(arrayOf(
                                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                            "text/csv",
                                            "*/*"
                                        ))
                                    }
                                ) {
                                    Icon(Icons.Default.FileUpload, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("رفع ملف إكسل")
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(
                            items = filteredArticles,
                            key = { _, article -> article.id }
                        ) { index, article ->
                            ArticleCardItem(
                                article = article,
                                displayIndex = index + 1,
                                isManualSort = sortMode == ArticleSortMode.MANUAL,
                                canMoveUp = index > 0 && sortMode == ArticleSortMode.MANUAL,
                                canMoveDown = index < filteredArticles.size - 1 && sortMode == ArticleSortMode.MANUAL,
                                onMoveUp = { viewModel.moveArticleUp(article) },
                                onMoveDown = { viewModel.moveArticleDown(article) },
                                onRead = { articleToRead = article },
                                onEdit = { articleToEdit = article },
                                onDelete = { articleToDelete = article },
                                onSpeak = { viewModel.speakEnglish(article.title) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog: Add / Edit Article
    if (isAddArticleOpen || articleToEdit != null) {
        val target = articleToEdit
        ArticleEditDialog(
            article = target,
            onDismiss = {
                isAddArticleOpen = false
                articleToEdit = null
            },
            onSave = { updatedArticle ->
                viewModel.insertOrUpdateArticle(updatedArticle)
                isAddArticleOpen = false
                articleToEdit = null
            }
        )
    }

    // Dialog: Full Reading View
    if (articleToRead != null) {
        ArticleReaderDialog(
            article = articleToRead!!,
            onDismiss = { articleToRead = null },
            onSpeak = { text -> viewModel.speakEnglish(text) },
            onEdit = {
                val toEdit = articleToRead
                articleToRead = null
                articleToEdit = toEdit
            }
        )
    }

    // Dialog: Delete Confirmation
    if (articleToDelete != null) {
        val art = articleToDelete!!
        AlertDialog(
            onDismissRequest = { articleToDelete = null },
            title = { Text("تأكيد حذف المقال") },
            text = { Text("هل أنت متأكد من رغبتك في حذف مقال \"${art.title}\" نهائياً؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteArticle(art.id)
                        articleToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { articleToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Dialog: Excel Template Format Info
    if (showTemplateInfoDialog) {
        AlertDialog(
            onDismissRequest = { showTemplateInfoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HelpOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تنسيق ملف إكسل لاستيراد المقالات")
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "يمكنك رفع ملف Excel (.xlsx) أو CSV يحتوي على أعمدة بالمسميات التالية (بالعربية أو الإنجليزية):",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val cols = listOf(
                        "العنوان (Title)" to "اسم أو عنوان المقال",
                        "المستوى (Level)" to "مستوى المقال (A1, A2, B1, B2, C1, C2)",
                        "التصنيف (Category)" to "فئة المقال (مثال: تعليم، علوم، قصص)",
                        "النص الكامل (Content)" to "المحتوى والنص الكامل للمقال بالإنجليزية",
                        "الكلمات المفتاحية (Keywords)" to "الكلمات المفتاحية للمقال مفصولة بفواصل"
                    )

                    cols.forEach { (name, desc) ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "يدعم التطبيق الترميز العربي UTF-8 و UTF-8 with BOM و Windows-1256 تلقائياً بدون أي تشويه للحروف.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF059669)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showTemplateInfoDialog = false }) {
                    Text("حسناً، فهمت")
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ArticleCardItem(
    article: Article,
    displayIndex: Int,
    isManualSort: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRead: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSpeak: () -> Unit
) {
    val wordCount = remember(article.content) {
        article.content.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
    }
    val readingTimeMinutes = maxOf(1, (wordCount / 130))

    val levelColor = when (article.level.uppercase()) {
        "A1", "A2" -> Color(0xFF059669)
        "B1", "B2" -> Color(0xFF2563EB)
        "C1", "C2" -> Color(0xFF7C3AED)
        else -> Color(0xFF4B5563)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRead() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top row: Index badge, level badge, category badge, and Up/Down reordering
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Order badge
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4338CA).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#$displayIndex",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF4338CA)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Level Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = levelColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, levelColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = article.level.uppercase(),
                            color = levelColor,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Category Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = article.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Reordering buttons (Manual sorting)
                if (isManualSort) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onMoveUp,
                            enabled = canMoveUp,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "تحريك للأعلى",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = onMoveDown,
                            enabled = canMoveDown,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "تحريك للأسفل",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Article Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onSpeak,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "استماع للعنوان",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Word count and reading time info
            Text(
                text = "$wordCount كلمة • حوالي $readingTimeMinutes دقيقة قراءة",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Content excerpt preview
            Text(
                text = article.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            // Keywords chips
            if (article.keywords.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    article.keywords.split(",", "،").map { it.trim() }.filter { it.isNotBlank() }.take(5).forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Read full article button
                Button(
                    onClick = onRead,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4338CA)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("قراءة المقال", style = MaterialTheme.typography.labelMedium)
                }

                Row {
                    // Edit button
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "تعديل",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Delete button
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
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

@Composable
private fun ArticleReaderDialog(
    article: Article,
    onDismiss: () -> Unit,
    onSpeak: (String) -> Unit,
    onEdit: () -> Unit
) {
    var fontSizeSp by remember { mutableFloatStateOf(16f) }
    val clipboard = LocalClipboardManager.current

    val wordCount = remember(article.content) {
        article.content.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = article.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "المستوى: ${article.level} • ${article.category} • $wordCount كلمة",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Font Size controls: A- and A+
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (fontSizeSp > 12f) fontSizeSp -= 2f },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("A-", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        IconButton(
                            onClick = { if (fontSizeSp < 26f) fontSizeSp += 2f },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("A+", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Quick Action bar: Listen & Copy
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onSpeak(article.content) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("استماع للمقال", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                clipboard.setText(AnnotatedString("${article.title}\n\n${article.content}"))
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("نسخ", fontSize = 12.sp)
                        }
                    }

                    // Full Article Content
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = article.content,
                            fontSize = fontSizeSp.sp,
                            lineHeight = (fontSizeSp * 1.6f).sp,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textDirection = TextDirection.Ltr
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    // Keywords if present
                    if (article.keywords.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "الكلمات المفتاحية: ${article.keywords}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("إغلاق")
                }
            },
            dismissButton = {
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تعديل")
                }
            }
        )
    }
}

@Composable
private fun ArticleEditDialog(
    article: Article?,
    onDismiss: () -> Unit,
    onSave: (Article) -> Unit
) {
    var title by remember { mutableStateOf(article?.title.orEmpty()) }
    var level by remember { mutableStateOf(article?.level ?: "B1") }
    var category by remember { mutableStateOf(article?.category ?: "عام") }
    var content by remember { mutableStateOf(article?.content.orEmpty()) }
    var keywords by remember { mutableStateOf(article?.keywords.orEmpty()) }

    val wordCount = remember(content) {
        content.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
    }

    val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = if (article == null) "إضافة مقال تعليمي جديد" else "تعديل المقال",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(440.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Title field
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان المقال (Title)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Level Selector Row
                    Text("المستوى (Level):", style = MaterialTheme.typography.bodySmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        levels.forEach { lvl ->
                            FilterChip(
                                selected = level == lvl,
                                onClick = { level = lvl },
                                label = { Text(lvl, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Category field
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("التصنيف (Category)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Keywords field
                    OutlinedTextField(
                        value = keywords,
                        onValueChange = { keywords = it },
                        label = { Text("الكلمات المفتاحية (مفصولة بفواصل)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Content field with live word counter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("النص الكامل للمقال:", style = MaterialTheme.typography.bodySmall)
                        Text(
                            text = "$wordCount كلمة",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        placeholder = { Text("اكتب أو الصق النص الكامل للمقال هنا...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            textDirection = TextDirection.Ltr
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val updated = article?.copy(
                                title = title.trim(),
                                level = level,
                                category = category.trim(),
                                content = content.trim(),
                                keywords = keywords.trim(),
                                updatedAt = System.currentTimeMillis()
                            ) ?: Article(
                                id = 0L,
                                title = title.trim(),
                                level = level,
                                category = category.trim(),
                                content = content.trim(),
                                keywords = keywords.trim(),
                                orderIndex = 999
                            )
                            onSave(updated)
                        }
                    },
                    enabled = title.isNotBlank()
                ) {
                    Text("حفظ المقال", fontWeight = FontWeight.Bold)
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
