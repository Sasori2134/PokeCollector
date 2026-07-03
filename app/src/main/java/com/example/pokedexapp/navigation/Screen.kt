package com.example.pokedexapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.CatchingPokemon
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home: Screen("home", "Home", Icons.Default.Home)
    object Favorites: Screen("favorites", "Favorites", Icons.Default.Favorite)
    object Collected: Screen("collected", "Collected", Icons.Outlined.CatchingPokemon)
    object Profile: Screen("profile", "Profile", Icons.Default.Person)
    object Camera: Screen("camera", "Camera", Icons.Default.CameraAlt)
    object Login: Screen("login", "Login", Icons.AutoMirrored.Filled.Login)
    object Register: Screen("register", "Register", Icons.Default.PersonAdd)
}

val screens = listOf(Screen.Home, Screen.Favorites, Screen.Collected, Screen.Profile)