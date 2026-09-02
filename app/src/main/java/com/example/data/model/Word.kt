package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(
    tableName = "words",
    indices = [
        Index(value = ["isDeleted", "status"]),
        Index(value = ["isDeleted", "isFavorite"]),
        Index(value = ["isDeleted", "isMastered"]),
        Index(value = ["isDeleted", "category"]),
        Index(value = ["isDeleted", "level"]),
        Index(value = ["isDeleted", "nextReviewAt"]),
        Index(value = ["english"]),
        Index(value = ["arabic"])
    ]
)
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val english: String,
    val arabic: String,
    val category: String = "General",
    val subcategory: String = "",
    val level: String = "A1", // A1, A2, B1, B2, C1, C2
    val partOfSpeech: String = "Noun", // Noun, Verb, Adjective, Adverb, Idiom, Phrase, Preposition, Conjunction
    val pronunciation: String = "", // e.g. /ɪnˈvaɪrənmənt/
    val example: String = "",
    val exampleArabic: String = "",
    val audioUrl: String = "",
    val status: String = STATUS_NEW, // "NEW", "LEARNING", "REVIEW", "MASTERED"
    val isFavorite: Boolean = false,
    val isMastered: Boolean = false,
    val isDeleted: Boolean = false,
    val reviewCount: Int = 0,
    val lastReviewedAt: Long = 0L,
    val nextReviewAt: Long = 0L,
    val difficulty: Float = 0.3f, // 0.0 (easiest) to 1.0 (hardest)
    val intervalDays: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val STATUS_NEW = "NEW"
        const val STATUS_LEARNING = "LEARNING"
        const val STATUS_REVIEW = "REVIEW"
        const val STATUS_MASTERED = "MASTERED"

        val ALL_LEVELS = listOf("A1", "A2", "B1", "B2", "C1", "C2")
        val ALL_PARTS_OF_SPEECH = listOf(
            "Noun", "Verb", "Adjective", "Adverb", "Idiom", "Phrase", "Preposition", "Conjunction"
        )
        val ALL_CATEGORIES = listOf(
            "Daily Life",
            "Business",
            "Technology",
            "Science & Nature",
            "Travel & Culture",
            "Health & Mind",
            "Academic",
            "Idioms & Phrases",
            "General"
        )
    }
}
