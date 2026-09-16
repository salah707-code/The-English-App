package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIconHelper {

    val AVAILABLE_ICONS = listOf(
        "PrecisionManufacturing" to Icons.Default.PrecisionManufacturing,
        "Straighten" to Icons.Default.Straighten,
        "AccountBalance" to Icons.Default.AccountBalance,
        "ThumbUp" to Icons.Default.ThumbUp,
        "ThumbDown" to Icons.Default.ThumbDown,
        "Gavel" to Icons.Default.Gavel,
        "Visibility" to Icons.Default.Visibility,
        "DirectionsRun" to Icons.Default.DirectionsRun,
        "Star" to Icons.Default.Star,
        "Warning" to Icons.Default.Warning,
        "Forest" to Icons.Default.Forest,
        "Science" to Icons.Default.Science,
        "Schedule" to Icons.Default.Schedule,
        "Palette" to Icons.Default.Palette,
        "Apartment" to Icons.Default.Apartment,
        "Favorite" to Icons.Default.Favorite,
        "MusicNote" to Icons.Default.MusicNote,
        "Psychology" to Icons.Default.Psychology,
        "School" to Icons.Default.School,
        "Book" to Icons.Default.Book,
        "Computer" to Icons.Default.Computer,
        "SportsSoccer" to Icons.Default.SportsSoccer,
        "Flight" to Icons.Default.Flight,
        "LocalHospital" to Icons.Default.LocalHospital,
        "Restaurant" to Icons.Default.Restaurant,
        "Lightbulb" to Icons.Default.Lightbulb,
        "Work" to Icons.Default.Work,
        "Explore" to Icons.Default.Explore,
        "Pets" to Icons.Default.Pets,
        "RecordVoiceOver" to Icons.Default.RecordVoiceOver,
        "MenuBook" to Icons.Default.MenuBook,
        "Article" to Icons.Default.Article,
        "Forum" to Icons.Default.Forum,
        "AutoStories" to Icons.Default.AutoStories,
        "Description" to Icons.Default.Description,
        "Category" to Icons.Default.Category
    )

    val PRESET_COLORS = listOf(
        "#E11D48", // Rose
        "#DC2626", // Red
        "#EA580C", // Orange
        "#D97706", // Amber
        "#F59E0B", // Gold
        "#16A34A", // Green
        "#059669", // Emerald
        "#0D9488", // Teal
        "#0891B2", // Cyan
        "#0284C7", // Sky
        "#2563EB", // Blue
        "#4F46E5", // Indigo
        "#7C3AED", // Purple
        "#8B5CF6", // Violet
        "#C026D3", // Fuchsia
        "#DB2777", // Pink
        "#475569", // Slate
        "#1E293B"  // Dark Slate
    )

    fun getIcon(name: String): ImageVector {
        return AVAILABLE_ICONS.firstOrNull { it.first.equals(name, ignoreCase = true) }?.second
            ?: Icons.Default.Category
    }

    fun parseColor(hex: String, fallback: Color = Color(0xFF4F46E5)): Color {
        return try {
            val cleanHex = hex.trim().removePrefix("#")
            val colorInt = if (cleanHex.length == 6) {
                ("FF$cleanHex").toLong(16)
            } else if (cleanHex.length == 8) {
                cleanHex.toLong(16)
            } else {
                return fallback
            }
            Color(colorInt)
        } catch (_: Exception) {
            fallback
        }
    }
}
