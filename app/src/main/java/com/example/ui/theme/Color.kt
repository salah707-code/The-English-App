package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Neutral Surfaces Light
val BackgroundLight = Color(0xFFF8FAFC)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFF1F5F9)
val OnBackgroundLight = Color(0xFF0F172A)
val OnSurfaceLight = Color(0xFF0F172A)
val OnSurfaceVariantLight = Color(0xFF475569)
val OutlineLight = Color(0xFFE2E8F0)

// Neutral Surfaces Dark
val BackgroundDark = Color(0xFF0B0F19)
val SurfaceDark = Color(0xFF131B2E)
val SurfaceVariantDark = Color(0xFF1E293B)
val OnBackgroundDark = Color(0xFFF8FAFC)
val OnSurfaceDark = Color(0xFFF8FAFC)
val OnSurfaceVariantDark = Color(0xFF94A3B8)
val OutlineDark = Color(0xFF334155)

// Accent Colors Set
data class AccentPalette(
    val primaryLight: Color,
    val onPrimaryLight: Color,
    val primaryContainerLight: Color,
    val onPrimaryContainerLight: Color,
    val primaryDark: Color,
    val onPrimaryDark: Color,
    val primaryContainerDark: Color,
    val onPrimaryContainerDark: Color,
    val labelAr: String
)

val AccentPalettes: Map<String, AccentPalette> = mapOf(
    "BLUE" to AccentPalette(
        primaryLight = Color(0xFF2563EB),
        onPrimaryLight = Color(0xFFFFFFFF),
        primaryContainerLight = Color(0xFFDBEAFE),
        onPrimaryContainerLight = Color(0xFF1E3A8A),
        primaryDark = Color(0xFF60A5FA),
        onPrimaryDark = Color(0xFF0F172A),
        primaryContainerDark = Color(0xFF1E3A8A),
        onPrimaryContainerDark = Color(0xFFDBEAFE),
        labelAr = "أزرق قياسي"
    ),
    "GREEN" to AccentPalette(
        primaryLight = Color(0xFF059669),
        onPrimaryLight = Color(0xFFFFFFFF),
        primaryContainerLight = Color(0xFFD1FAE5),
        onPrimaryContainerLight = Color(0xFF064E3B),
        primaryDark = Color(0xFF34D399),
        onPrimaryDark = Color(0xFF0F172A),
        primaryContainerDark = Color(0xFF064E3B),
        onPrimaryContainerDark = Color(0xFFD1FAE5),
        labelAr = "أخضر طبيعي"
    ),
    "PURPLE" to AccentPalette(
        primaryLight = Color(0xFF7C3AED),
        onPrimaryLight = Color(0xFFFFFFFF),
        primaryContainerLight = Color(0xFFEDE9FE),
        onPrimaryContainerLight = Color(0xFF4C1D95),
        primaryDark = Color(0xFFA78BFA),
        onPrimaryDark = Color(0xFF0F172A),
        primaryContainerDark = Color(0xFF4C1D95),
        onPrimaryContainerDark = Color(0xFFEDE9FE),
        labelAr = "بنفسجي ملكي"
    ),
    "ORANGE" to AccentPalette(
        primaryLight = Color(0xFFEA580C),
        onPrimaryLight = Color(0xFFFFFFFF),
        primaryContainerLight = Color(0xFFFFEDD5),
        onPrimaryContainerLight = Color(0xFF7C2D12),
        primaryDark = Color(0xFFFB923C),
        onPrimaryDark = Color(0xFF0F172A),
        primaryContainerDark = Color(0xFF7C2D12),
        onPrimaryContainerDark = Color(0xFFFFEDD5),
        labelAr = "برتقالي حيوي"
    ),
    "RED" to AccentPalette(
        primaryLight = Color(0xFFDC2626),
        onPrimaryLight = Color(0xFFFFFFFF),
        primaryContainerLight = Color(0xFFFEE2E2),
        onPrimaryContainerLight = Color(0xFF7F1D1D),
        primaryDark = Color(0xFFF87171),
        onPrimaryDark = Color(0xFF0F172A),
        primaryContainerDark = Color(0xFF7F1D1D),
        onPrimaryContainerDark = Color(0xFFFEE2E2),
        labelAr = "أحمر قرمزي"
    ),
    "TURQUOISE" to AccentPalette(
        primaryLight = Color(0xFF0D9488),
        onPrimaryLight = Color(0xFFFFFFFF),
        primaryContainerLight = Color(0xFFCCFBF1),
        onPrimaryContainerLight = Color(0xFF134E4A),
        primaryDark = Color(0xFF2DD4BF),
        onPrimaryDark = Color(0xFF0F172A),
        primaryContainerDark = Color(0xFF134E4A),
        onPrimaryContainerDark = Color(0xFFCCFBF1),
        labelAr = "فيروزي هادئ"
    ),
    "NAVY" to AccentPalette(
        primaryLight = Color(0xFF1E293B),
        onPrimaryLight = Color(0xFFF8FAFC),
        primaryContainerLight = Color(0xFFE2E8F0),
        onPrimaryContainerLight = Color(0xFF0F172A),
        primaryDark = Color(0xFFE2E8F0),
        onPrimaryDark = Color(0xFF0F172A),
        primaryContainerDark = Color(0xFF1E293B),
        onPrimaryContainerDark = Color(0xFFF8FAFC),
        labelAr = "كحلي كلاسيكي"
    )
)

// Fallback primary defaults
val PrimaryLight = Color(0xFF2563EB)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFDBEAFE)
val OnPrimaryContainerLight = Color(0xFF1E3A8A)

val PrimaryDark = Color(0xFF60A5FA)
val OnPrimaryDark = Color(0xFF0F172A)
val PrimaryContainerDark = Color(0xFF1E3A8A)
val OnPrimaryContainerDark = Color(0xFFDBEAFE)

val SecondaryLight = Color(0xFF475569)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFF1F5F9)
val OnSecondaryContainerLight = Color(0xFF1E293B)

val SecondaryDark = Color(0xFF94A3B8)
val OnSecondaryDark = Color(0xFF0F172A)
val SecondaryContainerDark = Color(0xFF334155)
val OnSecondaryContainerDark = Color(0xFFF8FAFC)

val TertiaryLight = Color(0xFF334155)
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFE2E8F0)
val OnTertiaryContainerLight = Color(0xFF0F172A)

val TertiaryDark = Color(0xFFCBD5E1)
val OnTertiaryDark = Color(0xFF0F172A)
val TertiaryContainerDark = Color(0xFF1E293B)
val OnTertiaryContainerDark = Color(0xFFF8FAFC)

val StarGold = Color(0xFFF59E0B)
val ErrorColor = Color(0xFFEF4444)
val SuccessColor = Color(0xFF10B981)
