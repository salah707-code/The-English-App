package com.example.data.repository

import com.example.data.database.WordDao
import com.example.data.model.LearningStats
import com.example.data.model.Word
import com.example.data.preferences.AppPreferences
import com.example.data.sample.StarterVocabulary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class WordRepository(
    private val wordDao: WordDao,
    private val appPreferences: AppPreferences
) {

    val allActiveWords: Flow<List<Word>> = wordDao.getAllActiveWords()
    val favoriteWords: Flow<List<Word>> = wordDao.getFavoriteWords()
    val masteredWords: Flow<List<Word>> = wordDao.getMasteredWords()
    val trashWords: Flow<List<Word>> = wordDao.getTrashWords()

    fun getDueForReviewWords(): Flow<List<Word>> {
        val now = System.currentTimeMillis()
        return wordDao.getWordsDueForReview(now)
    }

    fun searchWords(query: String): Flow<List<Word>> {
        return if (query.isBlank()) {
            wordDao.getAllActiveWords()
        } else {
            wordDao.searchWords(query.trim())
        }
    }

    suspend fun initializeStarterDataIfEmpty() = withContext(Dispatchers.IO) {
        val count = wordDao.getTotalWordsCount().first()
        if (count == 0) {
            val starter = StarterVocabulary.getStarterWords()
            wordDao.insertAll(starter)
        }
    }

    suspend fun resetAndLoadStarterData() = withContext(Dispatchers.IO) {
        wordDao.clearAll()
        val starter = StarterVocabulary.getStarterWords()
        wordDao.insertAll(starter)
    }

    suspend fun getWordById(id: Long): Word? = withContext(Dispatchers.IO) {
        wordDao.getWordById(id)
    }

    suspend fun insertOrUpdateWord(word: Word): Long = withContext(Dispatchers.IO) {
        if (word.id == 0L) {
            wordDao.insertWord(word)
        } else {
            wordDao.updateWord(word)
            word.id
        }
    }

    suspend fun insertBatch(words: List<Word>) = withContext(Dispatchers.IO) {
        wordDao.insertAll(words)
    }

    suspend fun toggleFavorite(id: Long, currentIsFav: Boolean) = withContext(Dispatchers.IO) {
        wordDao.setFavorite(id, !currentIsFav)
    }

    suspend fun markAsMastered(id: Long) = withContext(Dispatchers.IO) {
        val word = wordDao.getWordById(id) ?: return@withContext
        wordDao.updateReviewResult(
            id = id,
            status = Word.STATUS_MASTERED,
            isMastered = true,
            nextReview = 0L,
            interval = 60,
            difficulty = (word.difficulty - 0.1f).coerceIn(0.1f, 1.0f)
        )
        appPreferences.recordLearningAction()
    }

    suspend fun markAsLearning(id: Long) = withContext(Dispatchers.IO) {
        val word = wordDao.getWordById(id) ?: return@withContext
        val nextReview = System.currentTimeMillis() + (1 * 24 * 60 * 60 * 1000L)
        wordDao.updateReviewResult(
            id = id,
            status = Word.STATUS_LEARNING,
            isMastered = false,
            nextReview = nextReview,
            interval = 1,
            difficulty = word.difficulty
        )
        appPreferences.recordLearningAction()
    }

    suspend fun recordFlashcardAnswer(id: Long, answerType: FlashcardAnswer) = withContext(Dispatchers.IO) {
        val word = wordDao.getWordById(id) ?: return@withContext
        val now = System.currentTimeMillis()

        when (answerType) {
            FlashcardAnswer.KNOWN -> {
                val nextInterval = calculateNextInterval(word.intervalDays, isCorrect = true)
                val nextReview = now + (nextInterval * 24 * 60 * 60 * 1000L)
                val newStatus = if (nextInterval >= 30) Word.STATUS_MASTERED else Word.STATUS_REVIEW
                val isMastered = newStatus == Word.STATUS_MASTERED
                val newDiff = (word.difficulty - 0.05f).coerceIn(0.1f, 1.0f)

                wordDao.updateReviewResult(
                    id = id,
                    status = newStatus,
                    isMastered = isMastered,
                    nextReview = nextReview,
                    interval = nextInterval,
                    difficulty = newDiff,
                    now = now
                )
            }
            FlashcardAnswer.REVIEW_LATER -> {
                val nextReview = now + (1 * 24 * 60 * 60 * 1000L)
                wordDao.updateReviewResult(
                    id = id,
                    status = Word.STATUS_REVIEW,
                    isMastered = false,
                    nextReview = nextReview,
                    interval = 1,
                    difficulty = word.difficulty,
                    now = now
                )
            }
            FlashcardAnswer.DONT_KNOW -> {
                val nextReview = now + (1 * 24 * 60 * 60 * 1000L)
                val newDiff = (word.difficulty + 0.1f).coerceIn(0.1f, 1.0f)
                wordDao.updateReviewResult(
                    id = id,
                    status = Word.STATUS_LEARNING,
                    isMastered = false,
                    nextReview = nextReview,
                    interval = 1,
                    difficulty = newDiff,
                    now = now
                )
            }
        }
        appPreferences.recordLearningAction()
    }

    private fun calculateNextInterval(currentInterval: Int, isCorrect: Boolean): Int {
        if (!isCorrect) return 1
        return when {
            currentInterval <= 1 -> 3
            currentInterval <= 3 -> 7
            currentInterval <= 7 -> 14
            currentInterval <= 14 -> 30
            currentInterval <= 30 -> 60
            else -> 90
        }
    }

    suspend fun moveToTrash(id: Long) = withContext(Dispatchers.IO) {
        wordDao.moveToTrash(id)
    }

    suspend fun restoreFromTrash(id: Long) = withContext(Dispatchers.IO) {
        wordDao.restoreFromTrash(id)
    }

    suspend fun deletePermanently(id: Long) = withContext(Dispatchers.IO) {
        wordDao.deletePermanently(id)
    }

    suspend fun emptyTrash() = withContext(Dispatchers.IO) {
        wordDao.emptyTrash()
    }

    suspend fun getRandomLearningCards(limit: Int): List<Word> = withContext(Dispatchers.IO) {
        val list = wordDao.getRandomLearningWords(limit)
        if (list.isNotEmpty()) list else wordDao.getRandomWords(limit)
    }

    suspend fun getRandomQuizQuestions(limit: Int): List<Word> = withContext(Dispatchers.IO) {
        wordDao.getRandomWords(limit)
    }

    suspend fun getAllWordsForExport(): List<Word> = withContext(Dispatchers.IO) {
        wordDao.getAllActiveWordsList()
    }

    suspend fun getAllWordsForBackup(): List<Word> = withContext(Dispatchers.IO) {
        wordDao.getAllWordsIncludingTrash()
    }

    suspend fun restoreDatabase(words: List<Word>) = withContext(Dispatchers.IO) {
        wordDao.clearAll()
        wordDao.insertAll(words)
    }

    fun getLearningStatsFlow(): Flow<LearningStats> {
        val now = System.currentTimeMillis()
        return combine(
            wordDao.getAllActiveWords(),
            wordDao.getWordsDueForReview(now),
            appPreferences.streakDays,
            appPreferences.wordsLearnedToday,
            appPreferences.dailyGoal
        ) { words, dueWords, streak, learnedToday, goal ->
            val total = words.size
            val newCount = words.count { it.status == Word.STATUS_NEW }
            val learningCount = words.count { it.status == Word.STATUS_LEARNING }
            val reviewCount = words.count { it.status == Word.STATUS_REVIEW }
            val masteredCount = words.count { it.status == Word.STATUS_MASTERED || it.isMastered }
            val favCount = words.count { it.isFavorite }

            val levels = words.groupingBy { it.level.uppercase() }.eachCount()
            val categories = words.groupingBy { it.category }.eachCount()

            LearningStats(
                totalWords = total,
                newWords = newCount,
                learningWords = learningCount,
                reviewWords = reviewCount,
                masteredWords = masteredCount,
                favoriteWords = favCount,
                dueForReviewToday = dueWords.size,
                streakDays = streak,
                wordsLearnedToday = learnedToday,
                dailyGoal = goal,
                levelDistribution = levels,
                categoryDistribution = categories
            )
        }
    }
}

enum class FlashcardAnswer {
    KNOWN,
    REVIEW_LATER,
    DONT_KNOW
}
