package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.CategoryEntity
import com.example.ui.theme.ErrorColor
import com.example.ui.theme.SuccessColor
import com.example.viewmodel.QuizQuestion
import com.example.viewmodel.QuizState
import com.example.viewmodel.QuizType
import com.example.viewmodel.WordViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: WordViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quizState by viewModel.quizState.collectAsStateWithLifecycle()
    val categories by viewModel.allCategories.collectAsStateWithLifecycle()
    val allWords by viewModel.allWords.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (quizState.isActive && !quizState.isFinished) {
                            quizState.questions.getOrNull(quizState.currentIndex)?.typeLabel
                                ?: stringResource(R.string.quiz_title)
                        } else {
                            stringResource(R.string.quiz_title)
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (quizState.isActive && !quizState.isFinished) {
                                viewModel.exitQuiz()
                            } else {
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.testTag("btn_quiz_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (quizState.isActive && !quizState.isFinished) {
                        // Combo streak pill
                        AnimatedVisibility(
                            visible = quizState.currentStreak >= 2,
                            enter = scaleIn() + fadeIn(),
                            exit = scaleOut() + fadeOut()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B)),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = "Streak",
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${quizState.currentStreak}x",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFFB45309)
                                    )
                                }
                            }
                        }

                        // Score Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Text(
                                text = "Score: ${quizState.score}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        if (!quizState.isActive || quizState.questions.isEmpty()) {
            QuizLobbyView(
                categories = categories,
                totalAvailableWords = allWords.size,
                onStartQuiz = { type, category, count, autoAdvance ->
                    viewModel.startQuiz(
                        type = type,
                        specificCategory = category,
                        questionCount = count,
                        autoAdvance = autoAdvance
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else if (quizState.isFinished) {
            QuizSummaryView(
                score = quizState.score,
                correctCount = quizState.correctCount,
                wrongCount = quizState.wrongCount,
                totalCount = quizState.questions.size,
                bestStreak = quizState.bestStreak,
                onRestart = {
                    viewModel.startQuiz(
                        type = quizState.quizType,
                        specificCategory = quizState.selectedCategoryName,
                        questionCount = quizState.questions.size,
                        autoAdvance = quizState.autoAdvance
                    )
                },
                onLobby = { viewModel.exitQuiz() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            ActiveQuizQuestionView(
                quizState = quizState,
                onSelectOption = { index -> viewModel.selectAndCheckQuizOption(index) },
                onLetterClick = { ch -> viewModel.updateSpelledAnswer(ch) },
                onBackspace = { viewModel.backspaceSpelledAnswer() },
                onClearSpelling = { viewModel.clearSpelledAnswer() },
                onCheckAnswer = { viewModel.checkQuizAnswer() },
                onNextQuestion = { viewModel.nextQuizQuestion() },
                onSpeak = { text -> viewModel.speakWord(text) },
                onToggleAutoAdvance = { enabled -> viewModel.setAutoAdvance(enabled) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

@Composable
fun QuizLobbyView(
    categories: List<CategoryEntity>,
    totalAvailableWords: Int,
    onStartQuiz: (type: QuizType, category: String?, count: Int, autoAdvance: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var questionCount by remember { mutableIntStateOf(10) }
    var autoAdvance by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ساحة الاختبارات التفاعلية",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "اختبر مفرداتك مع أسئلة اختيار من متعدد وتغذية راجعة فورية متحركة ($totalAvailableWords كلمة متوفرة)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // Quiz Configuration Options (Category, Question Count, Auto-Advance)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Category Filter Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "التصنيف:",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { showCategoryMenu = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedCategory ?: "جميع التصنيفات",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showCategoryMenu,
                                onDismissRequest = { showCategoryMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("جميع التصنيفات (كل الكلمات)") },
                                    onClick = {
                                        selectedCategory = null
                                        showCategoryMenu = false
                                    }
                                )
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.name) },
                                        onClick = {
                                            selectedCategory = cat.name
                                            showCategoryMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Question Count Selector
                    Column {
                        Text(
                            text = "عدد الأسئلة:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(5, 10, 15, 20).forEach { count ->
                                FilterChip(
                                    selected = questionCount == count,
                                    onClick = { questionCount = count },
                                    label = { Text("$count أسئلة") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }
                    }

                    // Auto Advance Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "الانتقال التلقائي للسؤال التالي",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "الانتقال تلقائياً بعد ثانيتين من اختيار الإجابة",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = autoAdvance,
                            onCheckedChange = { autoAdvance = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }

        // Section Title: Challenges
        item {
            Text(
                text = "أنماط الاختبارات المتوفرة",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // 1. Word to Meaning (Primary Focus)
        item {
            QuizModeCard(
                title = "من الكلمة إلى المعنى (Word → Meaning)",
                subtitle = "يعرض الكلمة بالإنجليزية مع النطق الصوتي وتختار المعنى العربي الصحيح من 4 خيارات فورية",
                badge = "الأساسي • شائع",
                icon = Icons.Default.Translate,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = { onStartQuiz(QuizType.EN_TO_AR, selectedCategory, questionCount, autoAdvance) }
            )
        }

        // 2. Meaning to Word (Primary Focus)
        item {
            QuizModeCard(
                title = "من المعنى إلى الكلمة (Meaning → Word)",
                subtitle = "يعرض المعنى العربي وتستحضر الكلمة الإنجليزية المناسبة من بين 4 خيارات متعددة",
                badge = "تحدي الاسترجاع",
                icon = Icons.Default.Quiz,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = { onStartQuiz(QuizType.AR_TO_EN, selectedCategory, questionCount, autoAdvance) }
            )
        }

        // 3. Mixed Arena (Alternates Word-to-Meaning and Meaning-to-Word)
        item {
            QuizModeCard(
                title = "تحدي شامل متنوع (Mixed Arena)",
                subtitle = "اختبار ديناميكي يتبادل عشوائياً بين الكلمة إلى المعنى والمعنى إلى الكلمة لتثبيت شامل",
                badge = "تحدي متقدم 🔥",
                icon = Icons.Default.Shuffle,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                onClick = { onStartQuiz(QuizType.MIXED, selectedCategory, questionCount, autoAdvance) }
            )
        }

        // 4. Listening Challenge
        item {
            QuizModeCard(
                title = "تحدي الاستماع الصوتي 🎧",
                subtitle = "استمع إلى النطق الصوتي الدقيق واختر المعنى والترجمة الصحيحة مباشرة",
                badge = "نطق واستماع",
                icon = Icons.Default.Headphones,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = { onStartQuiz(QuizType.LISTENING, selectedCategory, questionCount, autoAdvance) }
            )
        }

        // 5. Sentence Completion
        item {
            QuizModeCard(
                title = "إكمال الجمل في السياق ✍️",
                subtitle = "اقرأ الجملة النموذجية مع الفراغ واختر الكلمة المناسبة التي تكمل المعنى السياقي",
                badge = "سياق الجمل",
                icon = Icons.Default.CheckCircle,
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = { onStartQuiz(QuizType.SENTENCE_COMPLETION, selectedCategory, questionCount, autoAdvance) }
            )
        }

        // 6. Spelling Unscramble Challenge
        item {
            QuizModeCard(
                title = "تحدي التهجئة وتركيب الحروف 🔤",
                subtitle = "رتب الحروف المبعثرة بالإنجليزية لكتابة الكلمة الصحيحة المعبرة عن المعنى",
                badge = "مهارة الكتابة",
                icon = Icons.Default.Spellcheck,
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = { onStartQuiz(QuizType.SPELLING, selectedCategory, questionCount, autoAdvance) }
            )
        }

        // 7. Favorites Arena
        item {
            QuizModeCard(
                title = "اختبار الكلمات المفضلة ⭐",
                subtitle = "اختبار حصري ومخصص فقط للكلمات التي قمت بتمييزها بنجمة في قائمة المفضلة",
                badge = "مخصص لك",
                icon = Icons.Default.Star,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f),
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                onClick = { onStartQuiz(QuizType.FAVORITES, selectedCategory, questionCount, autoAdvance) }
            )
        }
    }
}

@Composable
fun QuizModeCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = contentColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = contentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = contentColor
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = contentColor.copy(alpha = 0.18f),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = contentColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.85f),
                lineHeight = 18.sp
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActiveQuizQuestionView(
    quizState: QuizState,
    onSelectOption: (Int) -> Unit,
    onLetterClick: (Char) -> Unit,
    onBackspace: () -> Unit,
    onClearSpelling: () -> Unit,
    onCheckAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onSpeak: (String) -> Unit,
    onToggleAutoAdvance: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val question = quizState.questions.getOrNull(quizState.currentIndex) ?: return
    val progress = ((quizState.currentIndex + 1).toFloat() / quizState.questions.size.toFloat()).coerceIn(0f, 1f)
    val optionLetters = listOf("A", "B", "C", "D")

    // Handle Auto-Advance after answer is evaluated
    LaunchedEffect(quizState.currentIndex, quizState.isAnswerChecked) {
        if (quizState.autoAdvance && quizState.isAnswerChecked) {
            delay(2000)
            onNextQuestion()
        }
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress & Step Indicator Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "سؤال ${quizState.currentIndex + 1} من ${quizState.questions.size}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (question.typeLabel.isNotBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = question.typeLabel.substringBefore("•").trim(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Animated Smooth Progress Bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Question Prompt Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Audio Speak Button for Listening mode or quick pronunciation
                if (question.type == QuizType.LISTENING) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .size(72.dp)
                            .clickable { onSpeak(question.word.english) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Listen Audio",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                } else if (question.type == QuizType.EN_TO_AR || question.type == QuizType.MIXED) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = { onSpeak(question.word.english) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Listen",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // Prompt Text
                Text(
                    text = question.prompt,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (question.prompt.length > 25) 22.sp else 28.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                // Sub-prompt / hints
                if (question.subPrompt.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = question.subPrompt,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Multiple Choice Options or Spelling UI
        if (question.type == QuizType.SPELLING) {
            // Spelling Display Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (quizState.spelledAnswer.isEmpty()) "المس الحروف للتركيب..." else quizState.spelledAnswer,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = if (quizState.spelledAnswer.isEmpty()) {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )

                    Row {
                        IconButton(onClick = onBackspace) {
                            Icon(imageVector = Icons.Default.Backspace, contentDescription = "Backspace")
                        }
                        IconButton(onClick = onClearSpelling) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scrambled Letter Tiles
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                question.scrambledLetters.forEach { ch ->
                    Button(
                        onClick = { onLetterClick(ch) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(
                            text = ch.uppercase(),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!quizState.isAnswerChecked) {
                Button(
                    onClick = onCheckAnswer,
                    enabled = quizState.spelledAnswer.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_check_answer"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "تحقق من الإجابة",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        } else {
            // Multiple Choice Options (4 Choices) with INSTANT Feedback Animations
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                question.options.forEachIndexed { index, optionText ->
                    val isSelected = quizState.selectedOptionIndex == index
                    val isCorrectChoice = index == question.correctIndex
                    val letter = optionLetters.getOrElse(index) { "${index + 1}" }

                    OptionCardItem(
                        optionText = optionText,
                        optionLetter = letter,
                        isSelected = isSelected,
                        isCorrectChoice = isCorrectChoice,
                        isAnswerChecked = quizState.isAnswerChecked,
                        onSelect = { onSelectOption(index) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Instant Feedback Animated Card (Slides up immediately after answering)
        AnimatedVisibility(
            visible = quizState.isAnswerChecked,
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (quizState.isCorrect) {
                        SuccessColor.copy(alpha = 0.12f)
                    } else {
                        ErrorColor.copy(alpha = 0.12f)
                    }
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.5.dp,
                    color = if (quizState.isCorrect) SuccessColor else ErrorColor
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Feedback Title with Icon & Points
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (quizState.isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (quizState.isCorrect) SuccessColor else ErrorColor,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (quizState.isCorrect) "إجابة صحيحة! أحسنت 🎉" else "إجابة غير صحيحة",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (quizState.isCorrect) SuccessColor else ErrorColor
                            )
                        }

                        if (quizState.isCorrect) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SuccessColor,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = "+10 نقاط",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Word Details Insight Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = question.word.english,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (question.word.pronunciation.isNotBlank()) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "[${question.word.pronunciation}]",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { onSpeak(question.word.english) },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Speak",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Text(
                                text = "المعنى: ${question.word.arabic}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (question.word.example.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "مثال: \"${question.word.example}\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (question.word.exampleArabic.isNotBlank()) {
                                    Text(
                                        text = question.word.exampleArabic,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Next Question Button
                    Button(
                        onClick = onNextQuestion,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_next_question"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (quizState.currentIndex + 1 >= quizState.questions.size) "عرض نتائج الاختبار" else "السؤال التالي",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OptionCardItem(
    optionText: String,
    optionLetter: String,
    isSelected: Boolean,
    isCorrectChoice: Boolean,
    isAnswerChecked: Boolean,
    onSelect: () -> Unit
) {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(isAnswerChecked, isSelected) {
        if (isAnswerChecked && isSelected && !isCorrectChoice) {
            // Rapid tactile shake wiggle animation on wrong answer
            shakeOffset.animateTo(-14f, tween(40))
            shakeOffset.animateTo(14f, tween(40))
            shakeOffset.animateTo(-10f, tween(40))
            shakeOffset.animateTo(10f, tween(40))
            shakeOffset.animateTo(-5f, tween(30))
            shakeOffset.animateTo(5f, tween(30))
            shakeOffset.animateTo(0f, tween(30))
        }
    }

    val scale by animateFloatAsState(
        targetValue = when {
            isAnswerChecked && isSelected && isCorrectChoice -> 1.03f
            isAnswerChecked && isCorrectChoice -> 1.02f
            isAnswerChecked && !isCorrectChoice && !isSelected -> 0.98f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "optionScale"
    )

    val containerColor by animateColorAsState(
        targetValue = when {
            isAnswerChecked && isCorrectChoice -> SuccessColor.copy(alpha = 0.16f)
            isAnswerChecked && isSelected && !isCorrectChoice -> ErrorColor.copy(alpha = 0.16f)
            isSelected -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(220),
        label = "containerColor"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isAnswerChecked && isCorrectChoice -> SuccessColor
            isAnswerChecked && isSelected && !isCorrectChoice -> ErrorColor
            isSelected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        },
        animationSpec = tween(220),
        label = "borderColor"
    )

    val textColor by animateColorAsState(
        targetValue = when {
            isAnswerChecked && isCorrectChoice -> SuccessColor
            isAnswerChecked && isSelected && !isCorrectChoice -> ErrorColor
            isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(220),
        label = "textColor"
    )

    val alpha = if (isAnswerChecked && !isCorrectChoice && !isSelected) 0.55f else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(x = shakeOffset.value.dp)
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = if (isAnswerChecked && (isCorrectChoice || isSelected)) 2.dp else 1.2.dp,
                color = borderColor,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(enabled = !isAnswerChecked, onClick = onSelect)
            .testTag("quiz_option_$optionLetter"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected || (isAnswerChecked && isCorrectChoice)) 3.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Letter Badge or Status Icon
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isAnswerChecked && isCorrectChoice -> SuccessColor
                            isAnswerChecked && isSelected && !isCorrectChoice -> ErrorColor
                            isSelected -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isAnswerChecked && isCorrectChoice) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Correct",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                } else if (isAnswerChecked && isSelected && !isCorrectChoice) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Wrong",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        text = optionLetter,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = optionText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isSelected || (isAnswerChecked && isCorrectChoice)) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 17.sp
                ),
                color = textColor.copy(alpha = alpha),
                modifier = Modifier.weight(1f)
            )

            // Dynamic badges on right
            if (isAnswerChecked) {
                if (isCorrectChoice) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SuccessColor.copy(alpha = 0.2f),
                        modifier = Modifier.padding(start = 6.dp)
                    ) {
                        Text(
                            text = "صحيحة ✓",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = SuccessColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                } else if (isSelected) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ErrorColor.copy(alpha = 0.2f),
                        modifier = Modifier.padding(start = 6.dp)
                    ) {
                        Text(
                            text = "اختيارك ✗",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ErrorColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuizSummaryView(
    score: Int,
    correctCount: Int,
    wrongCount: Int,
    totalCount: Int,
    bestStreak: Int,
    onRestart: () -> Unit,
    onLobby: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accuracy = if (totalCount > 0) ((correctCount.toFloat() / totalCount.toFloat()) * 100).toInt() else 0

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Trophy Icon
        Surface(
            shape = CircleShape,
            color = if (accuracy >= 80) Color(0xFFF59E0B).copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(96.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (accuracy >= 80) Icons.Default.EmojiEvents else Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = if (accuracy >= 80) Color(0xFFD97706) else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(52.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = when {
                accuracy >= 90 -> "أداء استثنائي وممتاز! 🏆"
                accuracy >= 75 -> "أحسنت! إنجاز رائع 👏"
                accuracy >= 50 -> "جيد جداً! واصل التعلّم 💪"
                else -> "بداية جيدة! بالممارسة ستتحسن 📚"
            },
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "حصلت على $score نقطة بدقة إجابة $accuracy%",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Breakdown Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "إجمالي الأسئلة:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "$totalCount أسئلة", fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "الإجابات الصحيحة:", color = SuccessColor)
                    Text(text = "$correctCount ($accuracy%)", fontWeight = FontWeight.Bold, color = SuccessColor)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "الإجابات الخاطئة:", color = ErrorColor)
                    Text(text = "$wrongCount", fontWeight = FontWeight.Bold, color = ErrorColor)
                }

                if (bestStreak > 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "أفضل سلسلة متتالية 🔥:", color = Color(0xFFD97706))
                        Text(text = "$bestStreak إجابات متتالية", fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "إعادة الاختبار (Play Again)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onLobby,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text = "العودة لساحة الاختبارات (Lobby)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        }
    }
}
