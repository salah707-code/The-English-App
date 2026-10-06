package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String, // اسم التصنيف بالعربية أو الإنجليزية
    val englishName: String = "",
    val colorHex: String = "#2563EB",
    val iconName: String = "Category",
    val orderIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        val DEFAULT_CATEGORIES: List<CategoryEntity> = listOf(
            CategoryEntity("cat_daily_life", "الحياة اليومية", "Daily Life", "#3B82F6", "Home", 0),
            CategoryEntity("cat_business", "الأعمال والتجارة", "Business", "#10B981", "BusinessCenter", 1),
            CategoryEntity("cat_travel", "السفر والسياحة", "Travel", "#F59E0B", "Flight", 2),
            CategoryEntity("cat_technology", "التكنولوجيا والتقنية", "Technology", "#8B5CF6", "Computer", 3),
            CategoryEntity("cat_education", "التعليم والدراسة", "Education", "#EC4899", "School", 4),
            CategoryEntity("cat_health", "الصحة والطب", "Health & Medicine", "#EF4444", "LocalHospital", 5),
            CategoryEntity("cat_food", "الطعام والشراب", "Food & Drinks", "#F97316", "Restaurant", 6),
            CategoryEntity("cat_work", "العمل والمهن", "Work & Career", "#06B6D4", "Work", 7),
            CategoryEntity("cat_science", "العلوم والطبيعة", "Science & Nature", "#14B8A6", "Science", 8),
            CategoryEntity("cat_family", "العائلة والمجتمع", "Family & Society", "#6366F1", "People", 9),
            CategoryEntity("cat_sports", "الرياضة واللياقة", "Sports & Fitness", "#84CC16", "FitnessCenter", 10),
            CategoryEntity("cat_arts", "الفنون والإعلام", "Arts & Media", "#A855F7", "Palette", 11),
            CategoryEntity("cat_environment", "البيئة والطقس", "Environment & Weather", "#22C55E", "WbSunny", 12),
            CategoryEntity("cat_general", "عام ومتنوع", "General", "#64748B", "Category", 13)
        )
    }
}
