package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.StudySession
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {

    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<StudySession>>

    @Query("SELECT COUNT(*) FROM study_sessions")
    fun getSessionsCount(): Flow<Int>

    @Query("SELECT SUM(durationSeconds) FROM study_sessions")
    fun getTotalStudyTimeSeconds(): Flow<Int?>

    @Query("SELECT SUM(correctCount) FROM study_sessions")
    fun getTotalCorrectAnswers(): Flow<Int?>

    @Query("SELECT SUM(totalQuestions) FROM study_sessions")
    fun getTotalQuestionsAnswered(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySession): Long

    @Query("DELETE FROM study_sessions")
    suspend fun clearAll()
}
