package com.dimensioncam.ui.navigation

/**
 * Navigation destinations for the app
 */
sealed class Screen(val route: String) {
    object Photos : Screen("photos")
    object Marking : Screen("marking/{photoId}") {
        fun createRoute(photoId: Long) = "marking/$photoId"
    }
    object Settings : Screen("settings")
}

/**
 * Bottom navigation tab items
 */
enum class BottomNavTab(val route: String) {
    PHOTOS("photos"),
    MARKING("marking"),
    SETTINGS("settings")
}
