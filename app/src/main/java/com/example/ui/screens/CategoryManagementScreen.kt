package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.ui.components.CategoryColorPickerDialog
import com.example.ui.components.CategoryIconHelper
import com.example.ui.components.CategoryIconPickerDialog
import com.example.ui.components.CategoryRenameDialog
import com.example.ui.components.CategoryReorderDialog
import com.example.viewmodel.WordViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    viewModel: WordViewModel,
    onNavigateBack: () -> Unit,
    onCategoryClick: (CategoryEntity) -> Unit
) {
    val categories by viewModel.allCategories.collectAsState()
    val wordCounts by viewModel.categoryWordCounts.collectAsState()
    val articlesCount by viewModel.articlesCount.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var editingColorCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var editingIconCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var renamingCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var isReorderDialogOpen by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val sortedCategories = remember(categories) {
        categories.sortedBy { it.orderIndex }
    }

    val filteredCategories = sortedCategories.filter { cat ->
        searchQuery.isBlank() ||
                cat.name.contains(searchQuery, ignoreCase = true) ||
                cat.englishName.contains(searchQuery, ignoreCase = true)
    }

    // Force RTL for full Arabic UX
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "إدارة التصنيفات والمفردات",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${categories.size} تصنيفاً معتمداً • شبكة البطاقات التفاعلية",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("btn_back_categories")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { isReorderDialogOpen = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "إعادة ترتيب البطاقات",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("تم الحفظ والمزامنة التلقائية مع قاعدة البيانات")
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = "تحديث"
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
                Spacer(modifier = Modifier.height(8.dp))

                // Search Bar for Categories
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_categories_input"),
                    placeholder = { Text("بحث في التصنيفات (مثال: آلات، طبيعة، أفعال...)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "بحث"
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Responsive Cards Grid
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("categories_grid")
                ) {
                    items(filteredCategories, key = { it.id }) { category ->
                        val index = sortedCategories.indexOfFirst { it.id == category.id }
                        val isArticles = category.id == "cat_19_articles" || category.name == "المقالات"
                        val isPhrases = category.id == "cat_18_common_phrases" || category.name == "العبارات الشائعة"
                        val count = if (isArticles) articlesCount else (wordCounts[category.name] ?: 0)
                        val unitLabel = when {
                            isArticles -> "مقال"
                            isPhrases -> "عبارة"
                            else -> "كلمة"
                        }

                        CategoryCardItem(
                            category = category,
                            displayIndex = category.orderIndex,
                            count = count,
                            unitLabel = unitLabel,
                            canMoveUp = index > 0,
                            canMoveDown = index >= 0 && index < sortedCategories.size - 1,
                            onMoveUp = { viewModel.moveCategoryUp(category) },
                            onMoveDown = { viewModel.moveCategoryDown(category) },
                            onClick = { onCategoryClick(category) },
                            onEditColor = { editingColorCategory = category },
                            onEditIcon = { editingIconCategory = category },
                            onRename = { renamingCategory = category }
                        )
                    }
                }
            }
        }

        // Category Reorder Dialog
        if (isReorderDialogOpen) {
            CategoryReorderDialog(
                categories = sortedCategories,
                onDismiss = { isReorderDialogOpen = false },
                onSaveOrder = { reordered ->
                    viewModel.reorderCategories(reordered)
                    scope.launch {
                        snackbarHostState.showSnackbar("تم حفظ الترتيب الجديد للبطاقات")
                    }
                },
                onResetDefault = {
                    viewModel.resetDefaultCategories()
                    scope.launch {
                        snackbarHostState.showSnackbar("تمت استعادة الترتيب الافتراضي")
                    }
                }
            )
        }

        // Customization Dialogs
        editingColorCategory?.let { cat ->
            CategoryColorPickerDialog(
                category = cat,
                onDismiss = { editingColorCategory = null },
                onColorSelected = { newHex ->
                    viewModel.updateCategoryColor(cat, newHex)
                    scope.launch {
                        snackbarHostState.showSnackbar("تم تحديث لون التصنيف إلى $newHex")
                    }
                }
            )
        }

        editingIconCategory?.let { cat ->
            CategoryIconPickerDialog(
                category = cat,
                onDismiss = { editingIconCategory = null },
                onIconSelected = { newIcon ->
                    viewModel.updateCategoryIcon(cat, newIcon)
                    scope.launch {
                        snackbarHostState.showSnackbar("تم تحديث أيقونة التصنيف")
                    }
                }
            )
        }

        renamingCategory?.let { cat ->
            CategoryRenameDialog(
                category = cat,
                onDismiss = { renamingCategory = null },
                onConfirmRename = { newName ->
                    viewModel.renameCategory(cat, newName)
                    scope.launch {
                        snackbarHostState.showSnackbar("تم تغيير اسم التصنيف إلى $newName")
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryCardItem(
    category: CategoryEntity,
    displayIndex: Int,
    count: Int,
    unitLabel: String,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onClick: () -> Unit,
    onEditColor: () -> Unit,
    onEditIcon: () -> Unit,
    onRename: () -> Unit
) {
    val categoryColor = CategoryIconHelper.parseColor(category.colorHex)
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("category_card_${category.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top Row: Icon Container + Word/Article Count Badge + Options Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Colored Icon Container with order number overlay
                Box {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = categoryColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = CategoryIconHelper.getIcon(category.iconName),
                                contentDescription = category.name,
                                tint = categoryColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    // Small order badge index on corner
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(categoryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$displayIndex",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            fontSize = 9.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Content Count Badge (مثال: 150 كلمة أو 12 مقال)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (count > 0) categoryColor.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "$count $unitLabel",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (count > 0) categoryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Options dropdown for color, icon, rename, move up/down
                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "خيارات البطاقة",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("تخصيص اللون (Color)") },
                                leadingIcon = {
                                    Icon(Icons.Default.ColorLens, contentDescription = null, tint = categoryColor)
                                },
                                onClick = {
                                    menuExpanded = false
                                    onEditColor()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("تغيير الأيقونة (Icon)") },
                                leadingIcon = {
                                    Icon(Icons.Default.Image, contentDescription = null, tint = categoryColor)
                                },
                                onClick = {
                                    menuExpanded = false
                                    onEditIcon()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("تعديل الاسم (Rename)") },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = categoryColor)
                                },
                                onClick = {
                                    menuExpanded = false
                                    onRename()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("تحريك للأمام (▲)") },
                                leadingIcon = {
                                    Icon(Icons.Default.ArrowUpward, contentDescription = null)
                                },
                                enabled = canMoveUp,
                                onClick = {
                                    menuExpanded = false
                                    onMoveUp()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("تحريك للخلف (▼)") },
                                leadingIcon = {
                                    Icon(Icons.Default.ArrowDownward, contentDescription = null)
                                },
                                enabled = canMoveDown,
                                onClick = {
                                    menuExpanded = false
                                    onMoveDown()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Arabic Name
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // English Subtitle
            if (category.englishName.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = category.englishName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visual Color accent line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(categoryColor, categoryColor.copy(alpha = 0.2f))
                        )
                    )
            )
        }
    }
}
