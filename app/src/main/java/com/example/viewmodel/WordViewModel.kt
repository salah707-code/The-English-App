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
    val scrambledLetters: List<Char> = emptyList()
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
    val isFinished: Boolean = false
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
    val repository = WordRepository(db.wordDao(), preferences)
    val importExportManager = DataImportExportManager(application)
    val ttsManager = TtsManager(application)

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
            repository.initializeStarterDataIfEmpty()
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
    fun startQuiz(type: QuizType, specificCategory: String? = null) {
        viewModelScope.launch {
            var pool = allWords.value
            if (type == QuizType.FAVORITES) {
                pool = favoriteWords.value
                if (pool.size < 4) {
                    pool = allWords.value
                }
            } else if (type == QuizType.CATEGORY && !specificCategory.isNullOrBlank()) {
                val catWords = pool.filter { it.category.equals(specificCategory, ignoreCase = true) }
                if (catWords.size >= 4) pool = catWords
            }

            val questionCount = 10.coerceAtMost(pool.size)
            val selectedSample = pool.shuffled().take(questionCount)
            val questions = selectedSample.map { word ->
                buildQuestionForWord(word, pool, type)
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
                isFinished = false
            )

            if (type == QuizType.LISTENING) {
                questions.firstOrNull()?.let { speakWord(it.word.english) }
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
                    subPrompt = target.pronunciation,
                    options = options,
                    correctIndex = options.indexOf(target.arabic),
                    word = target,
                    type = type
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
                    type = type
                )
            }
            QuizType.LISTENING -> {
                val options = (distractors.map { it.arabic } + target.arabic).shuffled()
                QuizQuestion(
                    prompt = "🎧 Listen & choose meaning",
                    subPrompt = "Tap audio button to replay",
                    options = options,
                    correctIndex = options.indexOf(target.arabic),
                    word = target,
                    type = type
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
                    type = type
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
                    scrambledLetters = letters
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
                    type = type
                )
            }
        }
    }

    fun selectQuizOption(index: Int) {
        if (_quizState.value.isAnswerChecked) return
        _quizState.value = _quizState.value.copy(selectedOptionIndex = index)
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

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
