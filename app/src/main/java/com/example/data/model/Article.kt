package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

/**
 * يمثل المقال التعليمي في التطبيق (بطاقة المقالات رقم 19).
 * يحتوي على: العنوان، المستوى، التصنيف، النص الكامل، والكلمات المفتاحية.
 */
@JsonClass(generateAdapter = true)
@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,                 // عنوان المقال
    val level: String = "",            // المستوى (تم إيقاف CEFR وفق متطلبات المشروع)
    val category: String = "عام",      // تصنيف المقال
    val content: String,               // النص الكامل للمقال
    val keywords: String = "",         // الكلمات المفتاحية مفصولة بفواصل
    val orderIndex: Int = 0,           // الترتيب لفرز المقالات وإعادة ترتيبها يدوياً
    val isDeleted: Boolean = false,    // سلة المهملات
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
