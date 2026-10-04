package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "QUIZ", "REVIEW", "FLASHCARDS"
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val totalQuestions: Int = 0,
    val correctCount: Int = 0,
    val score: Int = 0
)
