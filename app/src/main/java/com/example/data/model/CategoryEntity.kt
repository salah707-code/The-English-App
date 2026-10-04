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
        // التطبيق يبدأ فارغاً من أي تصنيفات مفروضة مسبقاً، والتصنيفات من إنشاء المستخدم
        val DEFAULT_CATEGORIES: List<CategoryEntity> = emptyList()
    }
}
