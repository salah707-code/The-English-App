package com.example.data.repository

import com.example.data.database.AffixDao
import com.example.data.database.ArticleDao
import com.example.data.database.CategoryDao
import com.example.data.database.WordDao
import com.example.data.model.AffixEntity
import com.example.data.model.Article
import com.example.data.model.CategoryEntity
import com.example.data.model.LearningStats
import com.example.data.model.Word
import com.example.data.preferences.AppPreferences
import com.example.data.sample.StarterArticles
import com.example.data.sample.StarterVocabulary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class WordRepository(
    private val wordDao: WordDao,
    private val categoryDao: CategoryDao,
    private val articleDao: ArticleDao,
    private val affixDao: AffixDao,
    private val appPreferences: AppPreferences
) {

    val allActiveWords: Flow<List<Word>> = wordDao.getAllActiveWords()
    val favoriteWords: Flow<List<Word>> = wordDao.getFavoriteWords()
    val masteredWords: Flow<List<Word>> = wordDao.getMasteredWords()
    val trashWords: Flow<List<Word>> = wordDao.getTrashWords()

    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val categoryWordCounts: Flow<Map<String, Int>> = categoryDao.getCategoryWordCounts()
        .map { list -> list.associate { it.category to it.count } }

    val allArticles: Flow<List<Article>> = articleDao.getAllArticles()
    val articlesCount: Flow<Int> = articleDao.getArticlesCount()

    val allPrefixes: Flow<List<AffixEntity>> = affixDao.getAffixesByType(AffixEntity.TYPE_PREFIX)
    val allSuffixes: Flow<List<AffixEntity>> = affixDao.getAffixesByType(AffixEntity.TYPE_SUFFIX)

    fun getWordsByCategory(category: String): Flow<List<Word>> {
        return wordDao.getWordsByCategory(category)
    }

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
        ensureDefaultCategories()
        val count = wordDao.getTotalWordsCount().first()
        if (count == 0) {
            val starter = StarterVocabulary.getStarterWords()
            wordDao.insertAll(starter)
        } else {
            // Check if category 18 ("العبارات الشائعة") has words, if not add them
            val phraseWords = wordDao.getWordsByCategory("العبارات الشائعة").first()
            if (phraseWords.isEmpty()) {
                val starterPhrases = StarterVocabulary.getStarterWords().filter { it.category == "العبارات الشائعة" }
                if (starterPhrases.isNotEmpty()) {
                    wordDao.insertAll(starterPhrases)
                }
            }
        }

        // Initialize starter articles if none exist
        val articlesCount = articleDao.getArticlesCountDirect()
        if (articlesCount == 0) {
            articleDao.insertAll(StarterArticles.getStarterArticles())
        }

        // Initialize starter prefixes & suffixes if none exist
        if (affixDao.getCount() == 0) {
            affixDao.insertAll(AffixEntity.DEFAULT_PREFIXES + AffixEntity.DEFAULT_SUFFIXES)
        }
    }

    suspend fun ensureDefaultCategories() = withContext(Dispatchers.IO) {
        val existing = categoryDao.getAllCategories().first()
        val existingIds = existing.map { it.id }.toSet()
        val toInsert = CategoryEntity.DEFAULT_CATEGORIES.filter { !existingIds.contains(it.id) }
        if (toInsert.isNotEmpty()) {
            categoryDao.insertAll(toInsert)
        }
    }

    suspend fun resetCategoriesToDefaults() = withContext(Dispatchers.IO) {
        categoryDao.insertAll(CategoryEntity.DEFAULT_CATEGORIES)
    }

    suspend fun reorderCategories(categories: List<CategoryEntity>) = withContext(Dispatchers.IO) {
        categoryDao.insertAll(categories)
    }

    suspend fun resetAndLoadStarterData() = withContext(Dispatchers.IO) {
        wordDao.clearAll()
        categoryDao.insertAll(CategoryEntity.DEFAULT_CATEGORIES)
        val starter = StarterVocabulary.getStarterWords()
        wordDao.insertAll(starter)
        articleDao.clearAll()
        articleDao.insertAll(StarterArticles.getStarterArticles())
        affixDao.deleteAll()
        affixDao.insertAll(AffixEntity.DEFAULT_PREFIXES + AffixEntity.DEFAULT_SUFFIXES)
    }

    // Affix (Prefix & Suffix) operations
    fun searchAffixes(query: String, type: String): Flow<List<AffixEntity>> {
        return if (query.isBlank()) {
            affixDao.getAffixesByType(type)
        } else {
            affixDao.searchAffixes(query.trim(), type)
        }
    }

    suspend fun insertAffixes(affixes: List<AffixEntity>) = withContext(Dispatchers.IO) {
        affixDao.insertAll(affixes)
    }

    suspend fun insertAffix(affix: AffixEntity) = withContext(Dispatchers.IO) {
        affixDao.insert(affix)
    }

    suspend fun updateAffix(affix: AffixEntity) = withContext(Dispatchers.IO) {
        affixDao.update(affix)
    }

    suspend fun deleteAffix(affix: AffixEntity) = withContext(Dispatchers.IO) {
        affixDao.delete(affix)
    }

    suspend fun resetAffixesToDefault() = withContext(Dispatchers.IO) {
        affixDao.deleteAll()
        affixDao.insertAll(AffixEntity.DEFAULT_PREFIXES + AffixEntity.DEFAULT_SUFFIXES)
    }

    // Article operations
    suspend fun insertOrUpdateArticle(article: Article): Long = withContext(Dispatchers.IO) {
        if (article.id == 0L) {
            articleDao.insertArticle(article)
        } else {
            articleDao.updateArticle(article)
            article.id
        }
    }

    suspend fun insertArticlesBatch(articles: List<Article>) = withContext(Dispatchers.IO) {
        articleDao.insertAll(articles)
    }

    suspend fun deleteArticle(id: Long) = withContext(Dispatchers.IO) {
        articleDao.deleteArticle(id)
    }

    suspend fun reorderArticles(articles: List<Article>) = withContext(Dispatchers.IO) {
        articleDao.updateAll(articles)
    }

    suspend fun insertOrUpdateCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        categoryDao.insertCategory(category)
    }

    suspend fun renameCategory(oldCategory: CategoryEntity, newName: String) = withContext(Dispatchers.IO) {
        val updated = oldCategory.copy(name = newName)
        categoryDao.updateCategory(updated)
        wordDao.renameCategoryInWords(oldCategory.name, newName)
    }

    suspend fun deleteCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        categoryDao.deleteCategory(category.id)
        wordDao.deleteWordsByCategory(category.name)
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
