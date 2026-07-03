package com.example.pokedexapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pokedexapp.screens.CollectedScreen
import com.example.pokedexapp.screens.FavoritesScreen
import com.example.pokedexapp.screens.HomeScreen
import com.example.pokedexapp.screens.LoginScreen
import com.example.pokedexapp.screens.ProfileScreen
import com.example.pokedexapp.screens.RegisterScreen
import com.example.pokedexapp.screens.CameraScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startDestination = if (currentUser != null) Screen.Home.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Collected.route) { CollectedScreen() }
        composable(Screen.Favorites.route) { FavoritesScreen() }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
        composable(Screen.Camera.route) { CameraScreen(navController) }
    }
}