package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.Word
import com.example.ui.components.getLevelColor
import com.example.ui.theme.SuccessColor
import com.example.viewmodel.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: WordViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.learningStats.collectAsStateWithLifecycle()
    var selectedTimeframe by remember { mutableStateOf("7 أيام") }
    var selectedBarIndex by remember { mutableStateOf<Int?>(null) }

    // Mock weekly study time (minutes per day) correlated with streak and learned words
    val dailyMinutes = remember(stats.streakDays, stats.wordsLearnedToday) {
        val base = listOf(22, 35, 18, 42, 28, 50, (stats.wordsLearnedToday * 3).coerceIn(15, 65))
        val days = listOf("السبت", "الأحد", "الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "اليوم")
        days.zip(base)
    }

    val totalWeeklyMinutes = dailyMinutes.sumOf { it.second }
    val avgDailyMinutes = if (dailyMinutes.isNotEmpty()) totalWeeklyMinutes / dailyMinutes.size else 0

    // Mastered words trajectory over last 7 sessions
    val masteredTrajectory = remember(stats.masteredWords) {
        val current = stats.masteredWords
        listOf(
            (current - 18).coerceAtLeast(0),
            (current - 14).coerceAtLeast(0),
            (current - 11).coerceAtLeast(0),
            (current - 8).coerceAtLeast(0),
            (current - 5).coerceAtLeast(0),
            (current - 2).coerceAtLeast(0),
            current
        )
    }

    // Force RTL for Arabic layout clarity
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "لوحة تقدم التعلم والإحصائيات",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "متابعة الكلمات المتقنة ووقت الدراسة اليومي",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("btn_stats_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
            modifier = modifier
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top KPI Summary Header: Mastered Words, Total Study Time, Streak
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Mastered Words KPI Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${stats.masteredWords}",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "كلمات تم إتقانها",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Total Study Time KPI Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${totalWeeklyMinutes} دقيقة",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "وقت الدراسة الأسبوعي",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Learning Streak KPI Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${stats.streakDays} أيام",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "حماسة الاستمرار",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                // Daily Study Time Bar Chart (مخطط وقت الدراسة اليومي)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.BarChart,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "وقت الدراسة اليومي (بالدقائق)",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "المعدل: $avgDailyMinutes دقيقة/يوم • الإجمالي: $totalWeeklyMinutes دقيقة",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "آخر 7 أيام",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Custom Bar Chart Canvas
                            val barPrimaryColor = MaterialTheme.colorScheme.primary
                            val barSecondaryColor = MaterialTheme.colorScheme.primaryContainer
                            val activeColor = MaterialTheme.colorScheme.tertiary
                            val maxMinutes = (dailyMinutes.maxOfOrNull { it.second } ?: 60).coerceAtLeast(60).toFloat()

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    dailyMinutes.forEachIndexed { index, (dayName, minutes) ->
                                        val isSelected = selectedBarIndex == index
                                        val heightRatio = (minutes.toFloat() / maxMinutes).coerceIn(0.12f, 1f)

                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { selectedBarIndex = if (isSelected) null else index }
                                        ) {
                                            // Value Label
                                            Text(
                                                text = "${minutes}د",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    fontSize = 10.sp
                                                ),
                                                color = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            // Bar Column
                                            Box(
                                                modifier = Modifier
                                                    .width(28.dp)
                                                    .height((130 * heightRatio).dp)
                                                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                                    .background(
                                                        if (isSelected) {
                                                            Brush.verticalGradient(listOf(activeColor, activeColor.copy(alpha = 0.6f)))
                                                        } else {
                                                            Brush.verticalGradient(listOf(barPrimaryColor, barPrimaryColor.copy(alpha = 0.5f)))
                                                        }
                                                    )
                                            )

                                            Spacer(modifier = Modifier.height(6.dp))

                                            // Day Label
                                            Text(
                                                text = dayName,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    fontSize = 10.sp
                                                ),
                                                color = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }

                            if (selectedBarIndex != null) {
                                val item = dailyMinutes[selectedBarIndex!!]
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "يوم ${item.first}: درست لمدة ${item.second} دقيقة (معدل إنجاز ممتاز)",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // Mastered Words Trajectory Trend Line Chart (منحنى تقدم الكلمات المتقنة)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.TrendingUp,
                                            contentDescription = null,
                                            tint = SuccessColor,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "منحنى نمو المفردات المتقنة",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "الوتيرة التصاعدية لإتقان الكلمات عبر الجلسات",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = SuccessColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "+${masteredTrajectory.last() - masteredTrajectory.first()} كلمة",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = SuccessColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Trend Line Curve Canvas
                            val strokeColor = SuccessColor
                            val fillColor = SuccessColor.copy(alpha = 0.15f)
                            val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val width = size.width
                                    val height = size.height
                                    val minVal = (masteredTrajectory.minOrNull() ?: 0).toFloat()
                                    val maxVal = (masteredTrajectory.maxOrNull() ?: 10).toFloat().coerceAtLeast(minVal + 1f)

                                    // Draw subtle grid lines
                                    val steps = 3
                                    for (i in 0..steps) {
                                        val y = height * (i.toFloat() / steps)
                                        drawLine(
                                            color = gridColor,
                                            start = Offset(0f, y),
                                            end = Offset(width, y),
                                            strokeWidth = 1.dp.toPx()
                                        )
                                    }

                                    // Build line path
                                    val points = masteredTrajectory.mapIndexed { index, value ->
                                        val x = width * (index.toFloat() / (masteredTrajectory.size - 1).coerceAtLeast(1))
                                        val norm = (value - minVal) / (maxVal - minVal)
                                        val y = height - (height * 0.8f * norm) - (height * 0.1f)
                                        Offset(x, y)
                                    }

                                    if (points.size >= 2) {
                                        // Fill under the curve
                                        val fillPath = Path().apply {
                                            moveTo(points.first().x, height)
                                            points.forEach { lineTo(it.x, it.y) }
                                            lineTo(points.last().x, height)
                                            close()
                                        }
                                        drawPath(path = fillPath, brush = Brush.verticalGradient(listOf(fillColor, Color.Transparent)))

                                        // Stroke curve line
                                        val linePath = Path().apply {
                                            moveTo(points.first().x, points.first().y)
                                            for (i in 1 until points.size) {
                                                lineTo(points[i].x, points[i].y)
                                            }
                                        }
                                        drawPath(path = linePath, color = strokeColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))

                                        // Draw points
                                        points.forEach { pt ->
                                            drawCircle(color = Color.White, radius = 5.dp.toPx(), center = pt)
                                            drawCircle(color = strokeColor, radius = 3.5.dp.toPx(), center = pt)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("قبل أسبوع", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("منتصف الأسبوع", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("اليوم (${stats.masteredWords} كلمة)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = SuccessColor)
                            }
                        }
                    }
                }

                // Overall Vocabulary Status Distribution (توزيع حالة المفردات)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "توزيع حالة المفردات في القاعدة",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "إجمالي المفردات: ${stats.totalWords} كلمة",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${(stats.progressRate * 100).toInt()}%",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Stacked Progress Bar
                            val masteredRatio = if (stats.totalWords > 0) stats.masteredWords.toFloat() / stats.totalWords else 0f
                            val learningRatio = if (stats.totalWords > 0) stats.learningWords.toFloat() / stats.totalWords else 0f
                            val reviewRatio = if (stats.totalWords > 0) stats.reviewWords.toFloat() / stats.totalWords else 0f
                            val newRatio = (1f - masteredRatio - learningRatio - reviewRatio).coerceAtLeast(0f)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                if (masteredRatio > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(masteredRatio)
                                            .height(12.dp)
                                            .background(SuccessColor)
                                    )
                                }
                                if (learningRatio > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(learningRatio)
                                            .height(12.dp)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                }
                                if (reviewRatio > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(reviewRatio)
                                            .height(12.dp)
                                            .background(MaterialTheme.colorScheme.tertiary)
                                    )
                                }
                                if (newRatio > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(newRatio)
                                            .height(12.dp)
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Status Legend
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                StatusRowItem(label = "كلمات متقنة (Mastered)", count = stats.masteredWords, total = stats.totalWords, color = SuccessColor)
                                StatusRowItem(label = "قيد التعلم (Learning)", count = stats.learningWords, total = stats.totalWords, color = MaterialTheme.colorScheme.primary)
                                StatusRowItem(label = "جاهزة للمراجعة (Review)", count = stats.reviewWords, total = stats.totalWords, color = MaterialTheme.colorScheme.tertiary)
                                StatusRowItem(label = "كلمات جديدة لم تبدأ (New)", count = stats.newWords, total = stats.totalWords, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                            }
                        }
                    }
                }

                // CEFR Levels Breakdown (توزيع المستويات)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "توزيع المستويات وفق الإطار الأوروبي (CEFR)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Word.ALL_LEVELS.forEach { level ->
                                val count = stats.levelDistribution[level] ?: 0
                                val pct = if (stats.totalWords > 0) (count.toFloat() / stats.totalWords.toFloat()) else 0f
                                val levelColor = getLevelColor(level)

                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = level,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = levelColor
                                        )
                                        Text(
                                            text = "$count كلمة (${(pct * 100).toInt()}%)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    LinearProgressIndicator(
                                        progress = { pct },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = levelColor,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
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
private fun StatusRowItem(
    label: String,
    count: Int,
    total: Int,
    color: Color
) {
    val percentage = if (total > 0) (count.toFloat() / total * 100).toInt() else 0
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = "$count كلمة ($percentage%)",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

