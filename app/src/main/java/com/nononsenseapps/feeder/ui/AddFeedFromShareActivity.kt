package com.nononsenseapps.feeder.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.nononsenseapps.feeder.base.DIAwareComponentActivity
import com.nononsenseapps.feeder.base.DIAwareViewModel
import com.nononsenseapps.feeder.ui.compose.editfeed.CreateFeedScreen
import com.nononsenseapps.feeder.ui.compose.navigation.AddFeedDestination
import com.nononsenseapps.feeder.ui.compose.searchfeed.SearchFeedScreen
import com.nononsenseapps.feeder.ui.compose.utils.withAllProviders

/**
 * This activity should only be started via a Send (share) or Open URL/Text intent.
 */
class AddFeedFromShareActivity : DIAwareComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val initialFeedUrl =
            (intent?.dataString ?: intent?.getStringExtra(Intent.EXTRA_TEXT))?.trim()

        setContent {
            val viewModel: AddFeedFromShareActivityViewModel = DIAwareViewModel()
            val currentTheme by viewModel.currentTheme.collectAsState()
            val darkThemePreference by viewModel.darkThemePreference.collectAsState()
            val dynamicColors by viewModel.dynamicColors.collectAsState()
            val textScale by viewModel.textScale.collectAsState()

            withAllProviders(
                currentTheme = currentTheme,
                darkThemePreference = darkThemePreference,
                dynamicColors = dynamicColors,
                textScale = textScale,
            ) {
                val navController = rememberAnimatedNavController()
                AnimatedNavHost(navController, startDestination = "search") {
                    composable(
                        "search",
                        enterTransition = { fadeIn() },
                        exitTransition = { fadeOut() },
                        popEnterTransition = { fadeIn() },
                        popExitTransition = { fadeOut() },
                    ) { backStackEntry ->
                        SearchFeedScreen(
                            onNavigateUp = {
                                onNavigateUpFromIntentActivities()
                            },
                            initialFeedUrl = initialFeedUrl,
                            searchFeedViewModel = backStackEntry.DIAwareViewModel()
                        ) {
                            AddFeedDestination.navigate(
                                navController,
                                feedUrl = it.url,
                                feedTitle = it.title
                            )
                        }
                    }
                    composable(
                        route = AddFeedDestination.route,
                        arguments = AddFeedDestination.arguments,
                        deepLinks = AddFeedDestination.deepLinks,
                        enterTransition = { fadeIn() },
                        exitTransition = { fadeOut() },
                        popEnterTransition = { fadeIn() },
                        popExitTransition = { fadeOut() },
                    ) { backStackEntry ->
                        CreateFeedScreen(
                            onNavigateUp = {
                                navController.popBackStack()
                            },
                            createFeedScreenViewModel = backStackEntry.DIAwareViewModel(),
                        ) {
                            finish()
                        }
                    }
                }
            }
        }
    }
}

fun Activity.onNavigateUpFromIntentActivities() {
    startActivity(
        Intent(
            this,
            MainActivity::class.java
        )
    )
    finish()
}
