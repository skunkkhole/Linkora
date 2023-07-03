package com.sakethh.linkora.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.datastore.preferences.preferencesKey
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sakethh.linkora.screens.collections.CollectionScreen
import com.sakethh.linkora.screens.collections.archiveScreen.ParentArchiveScreen
import com.sakethh.linkora.screens.collections.specificScreen.SpecificScreen
import com.sakethh.linkora.screens.home.HomeScreen
import com.sakethh.linkora.screens.settings.SettingsScreen
import com.sakethh.linkora.screens.settings.SettingsScreenVM

@Composable
fun MainNavigation(navController: NavHostController) {
    val startDestination = rememberSaveable {
        mutableStateOf(NavigationRoutes.HOME_SCREEN.name)
    }
    LaunchedEffect(key1 = Unit) {
        startDestination.value = if (SettingsScreenVM.Settings.readPreferenceValue(
                preferenceKey = preferencesKey(SettingsScreenVM.SettingsPreferences.HOME_SCREEN_VISIBILITY.name),
                dataStore = SettingsScreenVM.Settings.dataStore
            ) == true
        )
            NavigationRoutes.HOME_SCREEN.name
        else
            NavigationRoutes.COLLECTIONS_SCREEN.name
    }

    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.HOME_SCREEN.name
    ) {
        composable(route = NavigationRoutes.HOME_SCREEN.name) {
            HomeScreen()
        }
        composable(route = NavigationRoutes.COLLECTIONS_SCREEN.name) {
            CollectionScreen(navController = navController)
        }
        composable(route = NavigationRoutes.SETTINGS_SCREEN.name) {
            SettingsScreen(navController = navController)
        }
        composable(route = NavigationRoutes.SPECIFIC_SCREEN.name) {
            SpecificScreen(navController = navController)
        }
        composable(route = NavigationRoutes.ARCHIVE_SCREEN.name) {
            ParentArchiveScreen(navController = navController)
        }
    }
}