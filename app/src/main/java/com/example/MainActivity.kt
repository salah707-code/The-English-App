package com.example

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.model.CategoryEntity
import com.example.ui.components.AppBottomNav
import com.example.ui.navigation.Screen
import com.example.ui.screens.AffixesScreen
import com.example.ui.screens.AppLockScreen
import com.example.ui.screens.ArticlesManagementScreen
import com.example.ui.screens.CategoryDetailTableScreen
import com.example.ui.screens.CategoryManagementScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ImportExportScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.MasteredScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.ReviewScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.TrashScreen
import com.example.ui.screens.WordDetailScreen
import com.example.ui.screens.WordsScreen
import com.example.ui.theme.EnglishLearnTheme
import com.example.viewmodel.WordViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val viewModel: WordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val language by viewModel.preferences.language.collectAsStateWithLifecycle()
            val themeMode by viewModel.preferences.themeMode.collectAsStateWithLifecycle()
            val isAppLockEnabled by viewModel.isAppLockEnabled.collectAsStateWithLifecycle()
            val isSessionUnlocked by viewModel.isSessionUnlocked.collectAsStateWithLifecycle()

            // Update Configuration with dynamic locale
            val targetLocale = if (language == "ar") Locale("ar") else Locale("en")
            val config = Configuration(LocalConfiguration.current).apply {
                setLocale(targetLocale)
                setLayoutDirection(targetLocale)
            }
            Locale.setDefault(targetLocale)

            val layoutDirection = if (language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(
                LocalConfiguration provides config,
                LocalLayoutDirection provides layoutDirection
            ) {
                EnglishLearnTheme(themeMode = themeMode) {
                    if (isAppLockEnabled && !isSessionUnlocked) {
                        AppLockScreen(
                            viewModel = viewModel,
                            onUnlocked = { /* unlocked */ }
                        )
                    } else {
                        MainAppNavHost(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppNavHost(viewModel: WordViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        Screen.Home.route,
        Screen.Words.route,
        Screen.Categories.route,
        Screen.Learn.route,
        Screen.Quiz.route,
        Screen.Settings.route
    )

    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                AppBottomNav(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable(Screen.Words.route) {
                WordsScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable(Screen.WordDetail.route) {
                WordDetailScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Learn.route) {
                LearnScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Review.route) {
                ReviewScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> navController.navigate(route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Quiz.route) {
                QuizScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> navController.navigate(route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Mastered.route) {
                MasteredScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> navController.navigate(route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Trash.route) {
                TrashScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Stats.route) {
                StatsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Categories.route) {
                CategoryManagementScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onCategoryClick = { category ->
                        if (category.id == "cat_19_articles" || category.name == "المقالات") {
                            navController.navigate(Screen.Articles.route)
                        } else {
                            navController.navigate(Screen.CategoryDetail.createRoute(category.id))
                        }
                    }
                )
            }

            composable(Screen.Articles.route) {
                ArticlesManagementScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.CategoryDetail.route,
                arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                val categories by viewModel.allCategories.collectAsState()
                val selectedCategory = categories.firstOrNull { it.id == categoryId }
                    ?: CategoryEntity.DEFAULT_CATEGORIES.firstOrNull { it.id == categoryId }
                    ?: CategoryEntity(id = categoryId, name = "التصنيف", colorHex = "#4F46E5")

                CategoryDetailTableScreen(
                    category = selectedCategory,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ImportExport.route) {
                ImportExportScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Affixes.route) {
                AffixesScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> navController.navigate(route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
