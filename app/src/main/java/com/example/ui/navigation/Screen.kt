package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Words : Screen("words")
    object WordDetail : Screen("word_detail")
    object Learn : Screen("learn")
    object Review : Screen("review")
    object Quiz : Screen("quiz")
    object Favorites : Screen("favorites")
    object Mastered : Screen("mastered")
    object Trash : Screen("trash")
    object Stats : Screen("stats")
    object Categories : Screen("categories")
    object CategoryDetail : Screen("category_detail/{categoryId}") {
        fun createRoute(categoryId: String) = "category_detail/$categoryId"
    }
    object Articles : Screen("articles")
    object Affixes : Screen("affixes")
    object ImportExport : Screen("import_export")
    object Settings : Screen("settings")
}
