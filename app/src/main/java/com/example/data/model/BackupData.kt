package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BackupPayload(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val appVersion: String = "1.0",
    val words: List<Word> = emptyList(),
    val settings: Map<String, String> = emptyMap()
)

data class LearningStats(
    val totalWords: Int = 0,
    val newWords: Int = 0,
    val learningWords: Int = 0,
    val reviewWords: Int = 0,
    val masteredWords: Int = 0,
    val favoriteWords: Int = 0,
    val dueForReviewToday: Int = 0,
    val streakDays: Int = 1,
    val wordsLearnedToday: Int = 0,
    val dailyGoal: Int = 20,
    val levelDistribution: Map<String, Int> = emptyMap(),
    val categoryDistribution: Map<String, Int> = emptyMap()
) {
    val progressRate: Float
        get() = if (totalWords > 0) (masteredWords.toFloat() / totalWords.toFloat()).coerceIn(0f, 1f) else 0f

    val dailyProgressRate: Float
        get() = if (dailyGoal > 0) (wordsLearnedToday.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f) else 0f
}
