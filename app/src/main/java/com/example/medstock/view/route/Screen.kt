package com.example.medstock.view.route

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")       // Artboard 1
    object Login : Screen("login_screen")         // Artboard 2
    object Register : Screen("register_screen")   // Artboard 3
    object UserHome : Screen("user_home_screen")  // Artboard 4 & 5 (Gabungan Tab)
    object AdminHome : Screen("admin_home_screen")// Artboard 7 & 6 (Gabungan Tab)
}