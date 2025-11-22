package com.dimensioncam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dimensioncam.ui.marking.MarkingScreen
import com.dimensioncam.ui.marking.MarkingViewModel
import com.dimensioncam.ui.photos.PhotosScreen
import com.dimensioncam.ui.photos.PhotosViewModel
import com.dimensioncam.ui.settings.SettingsScreen
import com.dimensioncam.ui.settings.SettingsViewModel
import com.dimensioncam.ui.theme.DimensionCamTheme
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DimensionCamTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    // Use rememberSaveable with mutableStateOf to survive config changes and avoid compose runtime-specific APIs
    var selectedPhotoId by rememberSaveable { mutableStateOf(0L) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                selectedPhotoId = selectedPhotoId
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "photos",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("photos") {
                // Use LocalContext to get applicationContext safely in Compose
                val context = LocalContext.current
                val application = context.applicationContext as DimensionCamApplication
                 val viewModel: PhotosViewModel = viewModel(
                     factory = PhotosViewModel.Factory(application.photoRepository, application.settingsRepository)
                 )
                 PhotosScreen(
                     viewModel = viewModel,
                     onPhotoClick = { photoId ->
                         selectedPhotoId = photoId
                         navController.navigate("marking/$photoId")
                     }
                 )
             }

             composable(
                 route = "marking/{photoId}",
                 arguments = listOf(navArgument("photoId") { type = NavType.LongType })
             ) { backStackEntry ->
                 val photoId = backStackEntry.arguments?.getLong("photoId") ?: 0L
                val context = LocalContext.current
                val application = context.applicationContext as DimensionCamApplication
                 val viewModel: MarkingViewModel = viewModel(
                     factory = MarkingViewModel.Factory(application.photoRepository, application.settingsRepository, photoId)
                 )
                 MarkingScreen(
                     viewModel = viewModel,
                     onNavigateBack = { navController.popBackStack() }
                 )
             }

             composable("settings") {
                val context = LocalContext.current
                val application = context.applicationContext as DimensionCamApplication
                 val viewModel: SettingsViewModel = viewModel(
                     factory = SettingsViewModel.Factory(application.settingsRepository)
                 )
                 SettingsScreen(viewModel = viewModel)
             }
         }
     }
 }

 @Composable
 fun BottomNavigationBar(
     navController: NavHostController,
     selectedPhotoId: Long
 ) {
     val navBackStackEntry by navController.currentBackStackEntryAsState()
     val currentDestination = navBackStackEntry?.destination

     NavigationBar {
         NavigationBarItem(
             icon = { Icon(Icons.Filled.Image, contentDescription = null) },
             label = { Text(stringResource(R.string.tab_photos)) },
             selected = currentDestination?.hierarchy?.any { it.route == "photos" } == true,
             onClick = {
                 navController.navigate("photos") {
                     popUpTo(navController.graph.findStartDestination().id) {
                         saveState = true
                     }
                     launchSingleTop = true
                     restoreState = true
                 }
             }
         )

         NavigationBarItem(
             icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
             label = { Text(stringResource(R.string.tab_marking)) },
             selected = currentDestination?.hierarchy?.any { it.route?.startsWith("marking") == true } == true,
             onClick = {
                 if (selectedPhotoId > 0) {
                     navController.navigate("marking/$selectedPhotoId") {
                         popUpTo(navController.graph.findStartDestination().id) {
                             saveState = true
                         }
                         launchSingleTop = true
                         restoreState = true
                     }
                 }
             },
             enabled = selectedPhotoId > 0
         )

         NavigationBarItem(
             icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
             label = { Text(stringResource(R.string.tab_settings)) },
             selected = currentDestination?.hierarchy?.any { it.route == "settings" } == true,
             onClick = {
                 navController.navigate("settings") {
                     popUpTo(navController.graph.findStartDestination().id) {
                         saveState = true
                     }
                     launchSingleTop = true
                     restoreState = true
                 }
             }
         )
     }
 }
