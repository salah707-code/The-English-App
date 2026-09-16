package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.audio.TtsManager
import com.example.data.database.AppDatabase
import com.example.data.model.Article
import com.example.data.model.CategoryEntity
import com.example.data.model.LearningStats
import com.example.data.model.Word
import com.example.data.preferences.AppPreferences
import com.example.data.repository.FlashcardAnswer
import com.example.data.repository.WordRepository
import com.example.importexport.DataImportExportManager
import com.example.importexport.ImportPreview
import com.example.importexport.ImportResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOrder {
    AZ,
    ZA,
    LEVEL,
    RECENT,
    DUE_DATE
}

enum class QuizType {
    EN_TO_AR,
    AR_TO_EN,
    MIXED,
    LISTENING,
    SENTENCE_COMPLETION,
    SPELLING,
    CATEGORY,
    FAVORITES
}

data class QuizQuestion(
    val prompt: String,
    val subPrompt: String = "",
    val options: List<String>,
    val correctIndex: Int,
    val word: Word,
    val type: QuizType,
    val scrambledLetters: List<Char> = emptyList(),
    val typeLabel: String = ""
)

data class QuizState(
    val isActive: Boolean = false,
    val quizType: QuizType = QuizType.EN_TO_AR,
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val spelledAnswer: String = "",
    val isAnswerChecked: Boolean = false,
    val isCorrect: Boolean = false,
    val score: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val isFinished: Boolean = false,
    val autoAdvance: Boolean = false,
    val selectedCategoryName: String? = null
)

data class FlashcardState(
    val cards: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isFinished: Boolean = false,
    val knownCount: Int = 0,
    val reviewCount: Int = 0,
    val dontKnowCount: Int = 0
)

class WordViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val preferences = AppPreferences(application)
    val repository = WordRepository(db.wordDao(), db.categoryDao(), db.articleDao(), preferences)
    val importExportManager = DataImportExportManager(application)
    val ttsManager = TtsManager(application)

    // Category flows
    val allCategories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categoryWordCounts: StateFlow<Map<String, Int>> = repository.categoryWordCounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Article flows
    val allArticles: StateFlow<List<Article>> = repository.allArticles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val articlesCount: StateFlow<Int> = repository.articlesCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Filter and Sort states
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow<String?>(null)
    val selectedLevel = MutableStateFlow<String?>(null)
    val selectedPartOfSpeech = MutableStateFlow<String?>(null)
    val selectedStatusFilter = MutableStateFlow<String?>(null)
    val sortOrder = MutableStateFlow(SortOrder.AZ)

    // Flow of all active words
    val allWords: StateFlow<List<Word>> = repository.allActiveWords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flow of favorite words
    val favoriteWords: StateFlow<List<Word>> = repository.favoriteWords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flow of mastered words
    val masteredWords: StateFlow<List<Word>> = repository.masteredWords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flow of trash words
    val trashWords: StateFlow<List<Word>> = repository.trashWords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flow of due for review
    val dueForReviewWords: StateFlow<List<Word>> = repository.getDueForReviewWords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Learning Stats Flow
    val learningStats: StateFlow<LearningStats> = repository.getLearningStatsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LearningStats())

    data class FilterParams(
        val query: String,
        val category: String?,
        val level: String?,
        val partOfSpeech: String?,
        val status: String?,
        val sortOrder: SortOrder
    )

    private val filterParams: Flow<FilterParams> = combine(
        searchQuery,
        selectedCategory,
        selectedLevel,
        selectedPartOfSpeech,
        selectedStatusFilter
    ) { query: String, cat: String?, lvl: String?, pos: String?, status: String? ->
        FilterParams(query, cat, lvl, pos, status, SortOrder.AZ)
    }.combine(sortOrder) { params: FilterParams, sort: SortOrder ->
        params.copy(sortOrder = sort)
    }

    // Filtered and Sorted Words for Vocabulary Screen
    val filteredWords: StateFlow<List<Word>> = combine(allWords, filterParams) { words: List<Word>, params: FilterParams ->
        var list = words

        if (params.query.isNotBlank()) {
            val q = params.query.trim().lowercase()
            list = list.filter {
                it.english.lowercase().contains(q) ||
                it.arabic.contains(q) ||
                it.example.lowercase().contains(q) ||
                it.exampleArabic.contains(q)
            }
        }

        if (!params.category.isNullOrBlank()) {
            list = list.filter { it.category.equals(params.category, ignoreCase = true) }
        }

        if (!params.level.isNullOrBlank()) {
            list = list.filter { it.level.equals(params.level, ignoreCase = true) }
        }

        if (!params.partOfSpeech.isNullOrBlank()) {
            list = list.filter { it.partOfSpeech.equals(params.partOfSpeech, ignoreCase = true) }
        }

        if (!params.status.isNullOrBlank()) {
            when (params.status) {
                Word.STATUS_NEW -> list = list.filter { it.status == Word.STATUS_NEW }
                Word.STATUS_LEARNING -> list = list.filter { it.status == Word.STATUS_LEARNING }
                Word.STATUS_REVIEW -> list = list.filter { it.status == Word.STATUS_REVIEW }
                Word.STATUS_MASTERED -> list = list.filter { it.status == Word.STATUS_MASTERED || it.isMastered }
            }
        }

        when (params.sortOrder) {
            SortOrder.AZ -> list.sortedBy { it.english.lowercase() }
            SortOrder.ZA -> list.sortedByDescending { it.english.lowercase() }
            SortOrder.LEVEL -> list.sortedBy { it.level }
            SortOrder.RECENT -> list.sortedByDescending { it.updatedAt }
            SortOrder.DUE_DATE -> list.sortedBy { if (it.nextReviewAt == 0L) Long.MAX_VALUE else it.nextReviewAt }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Word for Detail
    private val _selectedWord = MutableStateFlow<Word?>(null)
    val selectedWord: StateFlow<Word?> = _selectedWord.asStateFlow()

    // Flashcard Learning Session State
    private val _flashcardState = MutableStateFlow(FlashcardState())
    val flashcardState: StateFlow<FlashcardState> = _flashcardState.asStateFlow()

    // Quiz Arena State
    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    // Import State
    val importPreview = MutableStateFlow<ImportPreview?>(null)
    val importResult = MutableStateFlow<ImportResult?>(null)
    val isImporting = MutableStateFlow(false)
    val userMessage = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            repository.ensureDefaultCategories()
            repository.initializeStarterDataIfEmpty()
        }
    }

    fun resetCategoriesToDefaults() {
        viewModelScope.launch {
            repository.resetCategoriesToDefaults()
        }
    }

    fun selectWord(word: Word?) {
        _selectedWord.value = word
    }

    fun toggleFavorite(word: Word) {
        viewModelScope.launch {
            repository.toggleFavorite(word.id, word.isFavorite)
            if (_selectedWord.value?.id == word.id) {
                _selectedWord.value = _selectedWord.value?.copy(isFavorite = !word.isFavorite)
            }
        }
    }

    fun markMastered(word: Word) {
        viewModelScope.launch {
            repository.markAsMastered(word.id)
            if (_selectedWord.value?.id == word.id) {
                _selectedWord.value = _selectedWord.value?.copy(
                    status = Word.STATUS_MASTERED,
                    isMastered = true
                )
            }
        }
    }

    fun markLearning(word: Word) {
        viewModelScope.launch {
            repository.markAsLearning(word.id)
            if (_selectedWord.value?.id == word.id) {
                _selectedWord.value = _selectedWord.value?.copy(
                    status = Word.STATUS_LEARNING,
                    isMastered = false
                )
            }
        }
    }

    fun saveWord(word: Word) {
        viewModelScope.launch {
            val id = repository.insertOrUpdateWord(word)
            _selectedWord.value = repository.getWordById(id)
        }
    }

    fun moveToTrash(word: Word) {
        viewModelScope.launch {
            repository.moveToTrash(word.id)
            if (_selectedWord.value?.id == word.id) {
                _selectedWord.value = null
            }
        }
    }

    fun restoreFromTrash(word: Word) {
        viewModelScope.launch {
            repository.restoreFromTrash(word.id)
        }
    }

    fun deletePermanently(word: Word) {
        viewModelScope.launch {
            repository.deletePermanently(word.id)
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            repository.emptyTrash()
        }
    }

    fun resetAndLoadSampleData() {
        viewModelScope.launch {
            repository.resetAndLoadStarterData()
            userMessage.value = "Sample vocabulary loaded!"
        }
    }

    // Audio Methods
    fun speakWord(text: String, accentOverride: String? = null) {
        if (!preferences.audioEnabled.value) return
        val accent = accentOverride ?: preferences.pronunciation.value
        ttsManager.speak(text, accent, preferences.speechRate.value)
    }

    fun speakSentence(text: String, accentOverride: String? = null) {
        if (!preferences.audioEnabled.value) return
        val accent = accentOverride ?: preferences.pronunciation.value
        ttsManager.speak(text, accent, preferences.speechRate.value)
    }

    // Flashcard Session Flow
    fun startFlashcardSession(category: String? = null, level: String? = null, dueOnly: Boolean = false) {
        viewModelScope.launch {
            val wordsList = if (dueOnly) {
                val due = dueForReviewWords.value
                if (due.isNotEmpty()) due else repository.getRandomLearningCards(20)
            } else {
                var pool = allWords.value
                if (preferences.hideMastered.value) {
                    pool = pool.filter { it.status != Word.STATUS_MASTERED && !it.isMastered }
                }
                if (!category.isNullOrBlank()) {
                    pool = pool.filter { it.category.equals(category, ignoreCase = true) }
                }
                if (!level.isNullOrBlank()) {
                    pool = pool.filter { it.level.equals(level, ignoreCase = true) }
                }
                pool.shuffled().take(preferences.dailyGoal.value.coerceAtLeast(10))
            }

            _flashcardState.value = FlashcardState(
                cards = if (wordsList.isNotEmpty()) wordsList else allWords.value.take(15),
                currentIndex = 0,
                isFlipped = false,
                isFinished = false,
                knownCount = 0,
                reviewCount = 0,
                dontKnowCount = 0
            )

            // Auto-speak first card if audio enabled
            val first = _flashcardState.value.cards.firstOrNull()
            first?.let { speakWord(it.english) }
        }
    }

    fun flipCard() {
        _flashcardState.value = _flashcardState.value.copy(
            isFlipped = !_flashcardState.value.isFlipped
        )
    }

    fun submitFlashcardAnswer(answer: FlashcardAnswer) {
        val state = _flashcardState.value
        val currentCard = state.cards.getOrNull(state.currentIndex) ?: return

        viewModelScope.launch {
            repository.recordFlashcardAnswer(currentCard.id, answer)

            val nextIndex = state.currentIndex + 1
            val isFinished = nextIndex >= state.cards.size

            _flashcardState.value = state.copy(
                currentIndex = nextIndex,
                isFlipped = false,
                isFinished = isFinished,
                knownCount = if (answer == FlashcardAnswer.KNOWN) state.knownCount + 1 else state.knownCount,
                reviewCount = if (answer == FlashcardAnswer.REVIEW_LATER) state.reviewCount + 1 else state.reviewCount,
                dontKnowCount = if (answer == FlashcardAnswer.DONT_KNOW) state.dontKnowCount + 1 else state.dontKnowCount
            )

            if (!isFinished) {
                state.cards.getOrNull(nextIndex)?.let { nextCard ->
                    speakWord(nextCard.english)
                }
            }
        }
    }

    // Quiz Session Flow
    fun startQuiz(
        type: QuizType,
        specificCategory: String? = null,
        questionCount: Int = 10,
        autoAdvance: Boolean = false
    ) {
        viewModelScope.launch {
            var pool = allWords.value
            if (type == QuizType.FAVORITES) {
                pool = favoriteWords.value
                if (pool.size < 4) {
                    pool = allWords.value
                }
            } else if ((type == QuizType.CATEGORY || !specificCategory.isNullOrBlank()) && !specificCategory.isNullOrBlank()) {
                val catWords = pool.filter { it.category.equals(specificCategory, ignoreCase = true) }
                if (catWords.size >= 4) pool = catWords
            }

            if (pool.isEmpty()) return@launch

            val count = questionCount.coerceAtMost(pool.size).coerceAtLeast(1)
            val selectedSample = pool.shuffled().take(count)
            val questions = selectedSample.mapIndexed { index, word ->
                val effectiveType = when (type) {
                    QuizType.MIXED -> if (index % 2 == 0) QuizType.EN_TO_AR else QuizType.AR_TO_EN
                    else -> type
                }
                buildQuestionForWord(word, pool, effectiveType)
            }

            _quizState.value = QuizState(
                isActive = true,
                quizType = type,
                questions = questions,
                currentIndex = 0,
                selectedOptionIndex = null,
                spelledAnswer = "",
                isAnswerChecked = false,
                isCorrect = false,
                score = 0,
                correctCount = 0,
                wrongCount = 0,
                currentStreak = 0,
                bestStreak = 0,
                isFinished = false,
                autoAdvance = autoAdvance,
                selectedCategoryName = specificCategory
            )

            val firstQ = questions.firstOrNull()
            if (firstQ?.type == QuizType.LISTENING) {
                speakWord(firstQ.word.english)
            }
        }
    }

    private fun buildQuestionForWord(target: Word, allPool: List<Word>, type: QuizType): QuizQuestion {
        val distractors = allPool.filter { it.id != target.id }.shuffled().take(3)

        return when (type) {
            QuizType.EN_TO_AR -> {
                val options = (distractors.map { it.arabic } + target.arabic).shuffled()
                QuizQuestion(
                    prompt = target.english,
                    subPrompt = target.pronunciation.ifBlank { "${target.level} • ${target.partOfSpeech}" },
                    options = options,
                    correctIndex = options.indexOf(target.arabic),
                    word = target,
                    type = type,
                    typeLabel = "Word → Meaning • كلمة إلى معنى"
                )
            }
            QuizType.AR_TO_EN -> {
                val options = (distractors.map { it.english } + target.english).shuffled()
                QuizQuestion(
                    prompt = target.arabic,
                    subPrompt = "${target.level} • ${target.partOfSpeech}",
                    options = options,
                    correctIndex = options.indexOf(target.english),
                    word = target,
                    type = type,
                    typeLabel = "Meaning → Word • معنى إلى كلمة"
                )
            }
            QuizType.MIXED -> {
                // Default fallback if called directly
                val options = (distractors.map { it.arabic } + target.arabic).shuffled()
                QuizQuestion(
                    prompt = target.english,
                    subPrompt = target.pronunciation.ifBlank { "${target.level} • ${target.partOfSpeech}" },
                    options = options,
                    correctIndex = options.indexOf(target.arabic),
                    word = target,
                    type = QuizType.EN_TO_AR,
                    typeLabel = "Word → Meaning • كلمة إلى معنى"
                )
            }
            QuizType.LISTENING -> {
                val options = (distractors.map { it.arabic } + target.arabic).shuffled()
                QuizQuestion(
                    prompt = "🎧 استمع واختر المعنى الصحيح",
                    subPrompt = "اضغط على رمز مكبر الصوت لسماع النطق مرة أخرى",
                    options = options,
                    correctIndex = options.indexOf(target.arabic),
                    word = target,
                    type = type,
                    typeLabel = "Listening Challenge • تحدي الاستماع"
                )
            }
            QuizType.SENTENCE_COMPLETION -> {
                val sentence = if (target.example.contains(target.english, ignoreCase = true)) {
                    target.example.replace(Regex("(?i)\\b${Regex.escape(target.english)}\\b"), "______")
                } else {
                    "______ (${target.arabic})"
                }
                val options = (distractors.map { it.english } + target.english).shuffled()
                QuizQuestion(
                    prompt = sentence,
                    subPrompt = target.exampleArabic,
                    options = options,
                    correctIndex = options.indexOf(target.english),
                    word = target,
                    type = type,
                    typeLabel = "Sentence Completion • إكمال الجملة"
                )
            }
            QuizType.SPELLING -> {
                val letters = target.english.trim().lowercase().filter { it.isLetter() }.toList().shuffled()
                QuizQuestion(
                    prompt = target.arabic,
                    subPrompt = "${target.level} • ${target.partOfSpeech}",
                    options = emptyList(),
                    correctIndex = 0,
                    word = target,
                    type = type,
                    scrambledLetters = letters,
                    typeLabel = "Spelling • تحدي التهجئة"
                )
            }
            QuizType.CATEGORY, QuizType.FAVORITES -> {
                val options = (distractors.map { it.arabic } + target.arabic).shuffled()
                QuizQuestion(
                    prompt = target.english,
                    subPrompt = "${target.category} • ${target.level}",
                    options = options,
                    correctIndex = options.indexOf(target.arabic),
                    word = target,
                    type = type,
                    typeLabel = if (type == QuizType.FAVORITES) "Favorites Quiz • اختبار المفضلة" else "Category Quiz • اختبار التصنيف"
                )
            }
        }
    }

    fun selectQuizOption(index: Int) {
        if (_quizState.value.isAnswerChecked) return
        _quizState.value = _quizState.value.copy(selectedOptionIndex = index)
    }

    fun selectAndCheckQuizOption(index: Int) {
        val state = _quizState.value
        if (state.isAnswerChecked) return
        val question = state.questions.getOrNull(state.currentIndex) ?: return

        val isCorrect = (index == question.correctIndex)
        val newStreak = if (isCorrect) state.currentStreak + 1 else 0
        val newBestStreak = maxOf(state.bestStreak, newStreak)
        val streakBonus = if (isCorrect && newStreak > 1) (newStreak - 1) * 2 else 0
        val pointsEarned = if (isCorrect) 10 + streakBonus else 0

        viewModelScope.launch {
            if (isCorrect) {
                repository.recordFlashcardAnswer(question.word.id, FlashcardAnswer.KNOWN)
            } else {
                repository.recordFlashcardAnswer(question.word.id, FlashcardAnswer.DONT_KNOW)
            }
        }

        _quizState.value = state.copy(
            selectedOptionIndex = index,
            isAnswerChecked = true,
            isCorrect = isCorrect,
            score = state.score + pointsEarned,
            correctCount = if (isCorrect) state.correctCount + 1 else state.correctCount,
            wrongCount = if (!isCorrect) state.wrongCount + 1 else state.wrongCount,
            currentStreak = newStreak,
            bestStreak = newBestStreak
        )

        // Pronounce English word upon answering
        if (isCorrect && question.type != QuizType.LISTENING) {
            speakWord(question.word.english)
        }
    }

    fun setAutoAdvance(enabled: Boolean) {
        _quizState.value = _quizState.value.copy(autoAdvance = enabled)
    }

    fun exitQuiz() {
        _quizState.value = QuizState(isActive = false)
    }

    fun updateSpelledAnswer(letter: Char) {
        if (_quizState.value.isAnswerChecked) return
        _quizState.value = _quizState.value.copy(
            spelledAnswer = _quizState.value.spelledAnswer + letter
        )
    }

    fun backspaceSpelledAnswer() {
        if (_quizState.value.isAnswerChecked) return
        val current = _quizState.value.spelledAnswer
        if (current.isNotEmpty()) {
            _quizState.value = _quizState.value.copy(
                spelledAnswer = current.dropLast(1)
            )
        }
    }

    fun clearSpelledAnswer() {
        if (_quizState.value.isAnswerChecked) return
        _quizState.value = _quizState.value.copy(spelledAnswer = "")
    }

    fun checkQuizAnswer() {
        val state = _quizState.value
        val question = state.questions.getOrNull(state.currentIndex) ?: return

        val isCorrect = if (state.quizType == QuizType.SPELLING) {
            state.spelledAnswer.trim().equals(question.word.english.trim(), ignoreCase = true)
        } else {
            state.selectedOptionIndex == question.correctIndex
        }

        viewModelScope.launch {
            if (isCorrect) {
                repository.recordFlashcardAnswer(question.word.id, FlashcardAnswer.KNOWN)
            } else {
                repository.recordFlashcardAnswer(question.word.id, FlashcardAnswer.DONT_KNOW)
            }
        }

        _quizState.value = state.copy(
            isAnswerChecked = true,
            isCorrect = isCorrect,
            score = if (isCorrect) state.score + 10 else state.score,
            correctCount = if (isCorrect) state.correctCount + 1 else state.correctCount,
            wrongCount = if (!isCorrect) state.wrongCount + 1 else state.wrongCount
        )
    }

    fun nextQuizQuestion() {
        val state = _quizState.value
        val nextIndex = state.currentIndex + 1
        if (nextIndex >= state.questions.size) {
            _quizState.value = state.copy(isFinished = true)
        } else {
            _quizState.value = state.copy(
                currentIndex = nextIndex,
                selectedOptionIndex = null,
                spelledAnswer = "",
                isAnswerChecked = false,
                isCorrect = false
            )
            val nextQ = state.questions.getOrNull(nextIndex)
            if (state.quizType == QuizType.LISTENING && nextQ != null) {
                speakWord(nextQ.word.english)
            }
        }
    }

    // Import Flow
    fun loadImportFile(uri: Uri) {
        viewModelScope.launch {
            try {
                val preview = importExportManager.parseFilePreview(uri)
                importPreview.value = preview
            } catch (e: Exception) {
                userMessage.value = "Failed to parse file: ${e.localizedMessage}"
            }
        }
    }

    fun executeImport(uri: Uri, mapping: Map<String, Int>, updateDuplicates: Boolean) {
        viewModelScope.launch {
            isImporting.value = true
            try {
                val existing = repository.getAllWordsForExport()
                val (result, wordsToSave) = importExportManager.processImport(
                    uri = uri,
                    columnMapping = mapping,
                    updateDuplicates = updateDuplicates,
                    existingWords = existing
                )
                repository.insertBatch(wordsToSave)
                importResult.value = result
                importPreview.value = null
            } catch (e: Exception) {
                userMessage.value = "Import failed: ${e.localizedMessage}"
            } finally {
                isImporting.value = false
            }
        }
    }

    fun getExportCsvString(scope: String, category: String?): String {
        var list = allWords.value
        when (scope) {
            "FAVORITES" -> list = favoriteWords.value
            "MASTERED" -> list = masteredWords.value
            "CATEGORY" -> if (!category.isNullOrBlank()) list = list.filter { it.category == category }
        }
        return importExportManager.exportToCsv(list)
    }

    fun getBackupJsonString(): String {
        val words = allWords.value + trashWords.value
        val settings = preferences.getAllSettingsMap()
        return importExportManager.createBackupJson(words, settings)
    }

    fun restoreBackup(uri: Uri) {
        viewModelScope.launch {
            try {
                val payload = importExportManager.restoreBackupJson(uri)
                if (payload != null && payload.words.isNotEmpty()) {
                    repository.restoreDatabase(payload.words)
                    preferences.restoreSettings(payload.settings)
                    userMessage.value = "Restored ${payload.words.size} words successfully!"
                } else {
                    userMessage.value = "Invalid backup file or empty words"
                }
            } catch (e: Exception) {
                userMessage.value = "Restore failed: ${e.localizedMessage}"
            }
        }
    }

    fun getWordsForCategory(categoryName: String): Flow<List<Word>> {
        return repository.getWordsByCategory(categoryName)
    }

    fun updateCategoryColor(category: CategoryEntity, newColorHex: String) {
        viewModelScope.launch {
            repository.insertOrUpdateCategory(category.copy(colorHex = newColorHex))
        }
    }

    fun updateCategoryIcon(category: CategoryEntity, newIconName: String) {
        viewModelScope.launch {
            repository.insertOrUpdateCategory(category.copy(iconName = newIconName))
        }
    }

    fun renameCategory(category: CategoryEntity, newName: String) {
        viewModelScope.launch {
            repository.renameCategory(category, newName)
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun addWordToCategory(
        category: String,
        english: String,
        arabic: String,
        meaning: String = "",
        level: String = "A1",
        pos: String = "Noun",
        example: String = "",
        exampleAr: String = ""
    ) {
        viewModelScope.launch {
            val word = Word(
                english = english.trim(),
                arabic = arabic.trim(),
                category = category,
                subcategory = meaning.trim(),
                level = level,
                partOfSpeech = pos,
                example = example.trim(),
                exampleArabic = exampleAr.trim(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.insertOrUpdateWord(word)
        }
    }

    fun updateWordInline(word: Word) {
        viewModelScope.launch {
            repository.insertOrUpdateWord(word.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteWordPermanently(id: Long) {
        viewModelScope.launch {
            repository.deletePermanently(id)
        }
    }

    fun importWordsIntoCategory(
        uri: Uri,
        categoryName: String,
        onComplete: (imported: Int, updated: Int) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val existing = repository.allActiveWords.first()
                val (result, words) = importExportManager.importCategoryWords(uri, categoryName, existing)
                if (words.isNotEmpty()) {
                    repository.insertBatch(words)
                    userMessage.value = "تم استيراد ${result.imported} كلمة بنجاح إلى تصنيف $categoryName"
                    onComplete(result.imported, result.updated)
                } else {
                    userMessage.value = "لم يتم العثور على كلمات صالحة في الملف"
                }
            } catch (e: Exception) {
                userMessage.value = "فشل الاستيراد: ${e.localizedMessage}"
            }
        }
    }

    fun importArticlesFromFile(
        uri: Uri,
        onComplete: (imported: Int) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val (result, articles) = importExportManager.importArticles(uri)
                if (articles.isNotEmpty()) {
                    repository.insertArticlesBatch(articles)
                    userMessage.value = "تم استيراد ${result.imported} مقالاً بنجاح"
                    onComplete(result.imported)
                } else {
                    userMessage.value = "لم يتم العثور على مقالات صالحة في الملف"
                }
            } catch (e: Exception) {
                userMessage.value = "فشل استيراد المقالات: ${e.localizedMessage}"
            }
        }
    }

    fun insertOrUpdateArticle(article: Article) {
        viewModelScope.launch {
            repository.insertOrUpdateArticle(article)
            userMessage.value = if (article.id == 0L) "تم إضافة المقال بنجاح" else "تم تحديث المقال بنجاح"
        }
    }

    fun deleteArticle(id: Long) {
        viewModelScope.launch {
            repository.deleteArticle(id)
            userMessage.value = "تم حذف المقال"
        }
    }

    fun moveArticleUp(article: Article) {
        viewModelScope.launch {
            val currentList = repository.allArticles.first()
            val index = currentList.indexOfFirst { it.id == article.id }
            if (index > 0) {
                val mutable = currentList.toMutableList()
                val prev = mutable[index - 1]
                mutable[index - 1] = article.copy(orderIndex = prev.orderIndex)
                mutable[index] = prev.copy(orderIndex = article.orderIndex)
                // Re-index cleanly
                val updated = mutable.mapIndexed { idx, art -> art.copy(orderIndex = idx + 1) }
                repository.reorderArticles(updated)
            }
        }
    }

    fun moveArticleDown(article: Article) {
        viewModelScope.launch {
            val currentList = repository.allArticles.first()
            val index = currentList.indexOfFirst { it.id == article.id }
            if (index >= 0 && index < currentList.size - 1) {
                val mutable = currentList.toMutableList()
                val next = mutable[index + 1]
                mutable[index + 1] = article.copy(orderIndex = next.orderIndex)
                mutable[index] = next.copy(orderIndex = article.orderIndex)
                val updated = mutable.mapIndexed { idx, art -> art.copy(orderIndex = idx + 1) }
                repository.reorderArticles(updated)
            }
        }
    }

    fun reorderArticles(articles: List<Article>) {
        viewModelScope.launch {
            val updated = articles.mapIndexed { idx, art -> art.copy(orderIndex = idx + 1) }
            repository.reorderArticles(updated)
        }
    }

    fun moveCategoryUp(category: CategoryEntity) {
        viewModelScope.launch {
            val currentList = repository.allCategories.first()
            val index = currentList.indexOfFirst { it.id == category.id }
            if (index > 0) {
                val mutable = currentList.toMutableList()
                val prev = mutable[index - 1]
                mutable[index - 1] = category.copy(orderIndex = prev.orderIndex)
                mutable[index] = prev.copy(orderIndex = category.orderIndex)
                val updated = mutable.mapIndexed { idx, cat -> cat.copy(orderIndex = idx + 1) }
                repository.reorderCategories(updated)
            }
        }
    }

    fun moveCategoryDown(category: CategoryEntity) {
        viewModelScope.launch {
            val currentList = repository.allCategories.first()
            val index = currentList.indexOfFirst { it.id == category.id }
            if (index >= 0 && index < currentList.size - 1) {
                val mutable = currentList.toMutableList()
                val next = mutable[index + 1]
                mutable[index + 1] = category.copy(orderIndex = next.orderIndex)
                mutable[index] = next.copy(orderIndex = category.orderIndex)
                val updated = mutable.mapIndexed { idx, cat -> cat.copy(orderIndex = idx + 1) }
                repository.reorderCategories(updated)
            }
        }
    }

    fun reorderCategories(categories: List<CategoryEntity>) {
        viewModelScope.launch {
            val updated = categories.mapIndexed { idx, cat -> cat.copy(orderIndex = idx + 1) }
            repository.reorderCategories(updated)
        }
    }

    fun resetCategoriesOrder() {
        viewModelScope.launch {
            val current = repository.allCategories.first()
            val orderMap = CategoryEntity.DEFAULT_CATEGORIES.mapIndexed { index, cat -> cat.id to (index + 1) }.toMap()
            val sorted = current.sortedBy { orderMap[it.id] ?: (it.orderIndex + 100) }
                .mapIndexed { idx, cat -> cat.copy(orderIndex = idx + 1) }
            repository.reorderCategories(sorted)
            userMessage.value = "تمت استعادة الترتيب الافتراضي للتصنيفات"
        }
    }

    fun resetDefaultCategories() {
        resetCategoriesOrder()
    }

    fun speakEnglish(text: String) {
        speakWord(text)
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
