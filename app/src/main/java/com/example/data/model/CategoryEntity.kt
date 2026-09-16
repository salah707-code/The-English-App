package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String, // Arabic category name (e.g. "آلات")
    val englishName: String = "",
    val colorHex: String = "#4F46E5",
    val iconName: String = "Category",
    val orderIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        val DEFAULT_CATEGORIES = listOf(
            CategoryEntity(
                id = "cat_1_machines",
                name = "آلات",
                englishName = "Machines & Tools",
                colorHex = "#E11D48",
                iconName = "PrecisionManufacturing",
                orderIndex = 1
            ),
            CategoryEntity(
                id = "cat_2_dimensions",
                name = "أبعاد و قياسات و أحجام",
                englishName = "Dimensions & Measures",
                colorHex = "#D97706",
                iconName = "Straighten",
                orderIndex = 2
            ),
            CategoryEntity(
                id = "cat_3_religions",
                name = "أديان",
                englishName = "Religions & Faith",
                colorHex = "#7C3AED",
                iconName = "AccountBalance",
                orderIndex = 3
            ),
            CategoryEntity(
                id = "cat_4_positive_actions",
                name = "أفعال إيجابية",
                englishName = "Positive Actions",
                colorHex = "#059669",
                iconName = "ThumbUp",
                orderIndex = 4
            ),
            CategoryEntity(
                id = "cat_5_negative_actions",
                name = "أفعال سلبية",
                englishName = "Negative Actions",
                colorHex = "#DC2626",
                iconName = "ThumbDown",
                orderIndex = 5
            ),
            CategoryEntity(
                id = "cat_6_management_law",
                name = "إدارة و سياسة و قانون",
                englishName = "Management & Law",
                colorHex = "#2563EB",
                iconName = "Gavel",
                orderIndex = 6
            ),
            CategoryEntity(
                id = "cat_7_sight_vision",
                name = "بصر و رؤية",
                englishName = "Sight & Vision",
                colorHex = "#0284C7",
                iconName = "Visibility",
                orderIndex = 7
            ),
            CategoryEntity(
                id = "cat_8_movements",
                name = "حركات",
                englishName = "Movements & Motion",
                colorHex = "#EA580C",
                iconName = "DirectionsRun",
                orderIndex = 8
            ),
            CategoryEntity(
                id = "cat_9_good_traits",
                name = "صفات جيدة",
                englishName = "Good Qualities",
                colorHex = "#F59E0B",
                iconName = "Star",
                orderIndex = 9
            ),
            CategoryEntity(
                id = "cat_10_negative_traits",
                name = "صفات سلبية",
                englishName = "Negative Traits",
                colorHex = "#991B1B",
                iconName = "Warning",
                orderIndex = 10
            ),
            CategoryEntity(
                id = "cat_11_nature",
                name = "طبيعة",
                englishName = "Nature & Environment",
                colorHex = "#16A34A",
                iconName = "Forest",
                orderIndex = 11
            ),
            CategoryEntity(
                id = "cat_12_science",
                name = "علوم",
                englishName = "Sciences & Tech",
                colorHex = "#4F46E5",
                iconName = "Science",
                orderIndex = 12
            ),
            CategoryEntity(
                id = "cat_13_time_period",
                name = "فترة زمنية",
                englishName = "Time & Eras",
                colorHex = "#0891B2",
                iconName = "Schedule",
                orderIndex = 13
            ),
            CategoryEntity(
                id = "cat_14_arts_literature",
                name = "فنون و آداب",
                englishName = "Arts & Literature",
                colorHex = "#C026D3",
                iconName = "Palette",
                orderIndex = 14
            ),
            CategoryEntity(
                id = "cat_15_buildings",
                name = "مباني",
                englishName = "Buildings & Architecture",
                colorHex = "#475569",
                iconName = "Apartment",
                orderIndex = 15
            ),
            CategoryEntity(
                id = "cat_16_emotions",
                name = "مشاعر و عواطف",
                englishName = "Emotions & Feelings",
                colorHex = "#DB2777",
                iconName = "Favorite",
                orderIndex = 16
            ),
            CategoryEntity(
                id = "cat_17_music_sounds",
                name = "موسيقى و أصوات",
                englishName = "Music & Sounds",
                colorHex = "#8B5CF6",
                iconName = "MusicNote",
                orderIndex = 17
            ),
            CategoryEntity(
                id = "cat_18_common_phrases",
                name = "العبارات الشائعة",
                englishName = "Common Phrases & Idioms",
                colorHex = "#0D9488",
                iconName = "RecordVoiceOver",
                orderIndex = 18
            ),
            CategoryEntity(
                id = "cat_19_articles",
                name = "المقالات",
                englishName = "Articles & Readings",
                colorHex = "#4338CA",
                iconName = "MenuBook",
                orderIndex = 19
            )
        )
    }
}
