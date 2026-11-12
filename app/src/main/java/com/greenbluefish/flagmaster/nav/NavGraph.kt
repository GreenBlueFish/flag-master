package com.greenbluefish.flagmaster.nav

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.greenbluefish.flagmaster.data.Category
import com.greenbluefish.flagmaster.data.Continent
import com.greenbluefish.flagmaster.explore.DetailScreen
import com.greenbluefish.flagmaster.explore.ExploreScreen
import com.greenbluefish.flagmaster.quiz.QuizCompleteScreen
import com.greenbluefish.flagmaster.quiz.QuizScreen
import com.greenbluefish.flagmaster.quiz.QuizSetupScreen
import com.greenbluefish.flagmaster.quiz.QuizViewModel
import com.greenbluefish.flagmaster.quiz.QuizViewModelFactory

/** Route constants for the single-activity nav graph. */
object Routes {
    const val HOME = "home"
    const val QUIZ_SETUP = "quiz_setup"
    const val QUIZ = "quiz"
    const val QUIZ_COMPLETE = "quiz_complete"
    const val EXPLORE = "explore"
    const val DETAIL = "detail/{key}"
    fun detail(key: String) = "detail/$key"
}

/**
 * Top-level navigation. The categories chosen on the setup screen are handed
 * to the quiz through a small shared holder (the quiz route itself carries no
 * args); the quiz's final score is held the same way for the completion screen.
 */
@Composable
fun NavGraph() {
    val navController = rememberNavController()

    // Lightweight cross-screen state survives recomposition via the NavGraph's
    // own remembered scope; selected categories flow setup -> quiz, and the
    // final score flows quiz -> completion.
    var selectedCategories by remember { mutableStateOf<Set<Category>>(emptySet()) }
    var selectedContinents by remember { mutableStateOf<Set<Continent>>(emptySet()) }
    var matchSelectionOnly by remember { mutableStateOf(true) }
    var finalScore by remember { mutableStateOf(0) }
    var finalTotal by remember { mutableStateOf(0) }

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onQuiz = { navController.navigate(Routes.QUIZ_SETUP) },
                onExplore = { navController.navigate(Routes.EXPLORE) },
            )
        }

        composable(Routes.QUIZ_SETUP) {
            QuizSetupScreen(
                onStart = { categories, continents, matchOnly ->
                    selectedCategories = categories
                    selectedContinents = continents
                    matchSelectionOnly = matchOnly
                    navController.navigate(Routes.QUIZ)
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.QUIZ) {
            // Guard against a direct entry with no selection.
            if (selectedCategories.isEmpty()) {
                navController.popBackStack(Routes.QUIZ_SETUP, inclusive = false)
            } else {
                val vm: QuizViewModel = viewModel(
                    factory = QuizViewModelFactory(
                        selectedCategories,
                        selectedContinents,
                        matchSelectionOnly,
                    ),
                )
                QuizScreen(
                    viewModel = vm,
                    onComplete = {
                        finalScore = vm.score
                        finalTotal = vm.total
                        navController.navigate(Routes.QUIZ_COMPLETE) {
                            popUpTo(Routes.QUIZ) { inclusive = true }
                        }
                    },
                )
            }
        }

        composable(Routes.QUIZ_COMPLETE) {
            QuizCompleteScreen(
                score = finalScore,
                total = finalTotal,
                onHome = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                },
            )
        }

        composable(Routes.EXPLORE) {
            ExploreScreen(
                onOpenDetail = { key -> navController.navigate(Routes.detail(key)) },
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("key") { type = NavType.StringType }),
        ) { backStackEntry ->
            val key = backStackEntry.arguments?.getString("key").orEmpty()
            DetailScreen(flagKey = key)
        }
    }
}

/** Landing screen: app title plus Quiz and Explore entry points. */
@Composable
fun HomeScreen(
    onQuiz: () -> Unit,
    onExplore: () -> Unit,
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        ) {
            Text(
                text = "Flag Master",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Learn the flags of the world, the US states, and the Canadian provinces.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(onClick = onQuiz, modifier = Modifier.fillMaxWidth()) {
                Text("Start a Quiz")
            }
            Button(onClick = onExplore, modifier = Modifier.fillMaxWidth()) {
                Text("Explore Flags")
            }
        }
    }
}
