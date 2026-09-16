package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles ORDER BY orderIndex ASC, id DESC")
    fun getAllArticles(): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE id = :id LIMIT 1")
    suspend fun getArticleById(id: Long): Article?

    @Query("SELECT * FROM articles WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR keywords LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY orderIndex ASC")
    fun searchArticles(query: String): Flow<List<Article>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: Article): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<Article>)

    @Update
    suspend fun updateArticle(article: Article)

    @Update
    suspend fun updateAll(articles: List<Article>)

    @Query("DELETE FROM articles WHERE id = :id")
    suspend fun deleteArticle(id: Long)

    @Query("DELETE FROM articles")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM articles")
    fun getArticlesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM articles")
    suspend fun getArticlesCountDirect(): Int
}
