package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Query("SELECT * FROM words WHERE isDeleted = 0 ORDER BY english ASC")
    fun getAllActiveWords(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE isDeleted = 0 AND status = :status ORDER BY english ASC")
    fun getWordsByStatus(status: String): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE isDeleted = 0 AND isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoriteWords(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE isDeleted = 0 AND (status = 'MASTERED' OR isMastered = 1) ORDER BY updatedAt DESC")
    fun getMasteredWords(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE isDeleted = 1 ORDER BY updatedAt DESC")
    fun getTrashWords(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE isDeleted = 0 AND nextReviewAt > 0 AND nextReviewAt <= :currentTime ORDER BY nextReviewAt ASC")
    fun getWordsDueForReview(currentTime: Long): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE isDeleted = 0 AND category = :category ORDER BY english ASC")
    fun getWordsByCategory(category: String): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE isDeleted = 0 AND level = :level ORDER BY english ASC")
    fun getWordsByLevel(level: String): Flow<List<Word>>

    @Query("""
        SELECT * FROM words 
        WHERE isDeleted = 0 
        AND (english LIKE '%' || :query || '%' OR arabic LIKE '%' || :query || '%' OR example LIKE '%' || :query || '%')
        ORDER BY english ASC
    """)
    fun searchWords(query: String): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE id = :id LIMIT 1")
    suspend fun getWordById(id: Long): Word?

    @Query("SELECT * FROM words WHERE english = :english AND isDeleted = 0 LIMIT 1")
    suspend fun getWordByEnglish(english: String): Word?

    @Query("SELECT * FROM words WHERE isDeleted = 0")
    suspend fun getAllActiveWordsList(): List<Word>

    @Query("SELECT * FROM words")
    suspend fun getAllWordsIncludingTrash(): List<Word>

    @Query("SELECT COUNT(*) FROM words WHERE isDeleted = 0")
    fun getTotalWordsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM words WHERE isDeleted = 0 AND status = 'NEW'")
    fun getNewWordsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM words WHERE isDeleted = 0 AND status = 'LEARNING'")
    fun getLearningWordsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM words WHERE isDeleted = 0 AND (status = 'MASTERED' OR isMastered = 1)")
    fun getMasteredWordsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM words WHERE isDeleted = 0 AND isFavorite = 1")
    fun getFavoritesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM words WHERE isDeleted = 0 AND nextReviewAt > 0 AND nextReviewAt <= :currentTime")
    fun getDueForReviewCount(currentTime: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM words WHERE isDeleted = 1")
    fun getTrashCount(): Flow<Int>

    @Query("SELECT * FROM words WHERE isDeleted = 0 AND status != 'MASTERED' ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomLearningWords(limit: Int): List<Word>

    @Query("SELECT * FROM words WHERE isDeleted = 0 ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomWords(limit: Int): List<Word>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<Word>)

    @Update
    suspend fun updateWord(word: Word)

    @Query("UPDATE words SET isFavorite = :isFavorite, updatedAt = :timestamp WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("""
        UPDATE words 
        SET status = :status, 
            isMastered = :isMastered, 
            reviewCount = reviewCount + 1, 
            lastReviewedAt = :now, 
            nextReviewAt = :nextReview, 
            intervalDays = :interval,
            difficulty = :difficulty,
            updatedAt = :now 
        WHERE id = :id
    """)
    suspend fun updateReviewResult(
        id: Long,
        status: String,
        isMastered: Boolean,
        nextReview: Long,
        interval: Int,
        difficulty: Float,
        now: Long = System.currentTimeMillis()
    )

    @Query("UPDATE words SET isDeleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun moveToTrash(id: Long, now: Long = System.currentTimeMillis())

    @Query("UPDATE words SET isDeleted = 0, updatedAt = :now WHERE id = :id")
    suspend fun restoreFromTrash(id: Long, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM words WHERE id = :id")
    suspend fun deletePermanently(id: Long)

    @Query("DELETE FROM words WHERE isDeleted = 1")
    suspend fun emptyTrash()

    @Query("DELETE FROM words")
    suspend fun clearAll()
}
