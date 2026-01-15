package com.example.medstock.view.uicontroller

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel

// --- IMPORT VIEW / HALAMAN ---
import com.example.medstock.view.*

// --- IMPORT ROUTE / DESTINASI ---
import com.example.medstock.view.route.Screen // Route Baru (Auth)
import com.example.medstock.view.route.DestinasiDetailResep
import com.example.medstock.view.route.DestinasiDetailResep.resepIdArg
import com.example.medstock.view.route.DestinasiEditObat
import com.example.medstock.view.route.DestinasiEditObat.obatIdArg
import com.example.medstock.view.route.DestinasiEditResep
import com.example.medstock.view.route.DestinasiEntryObat
import com.example.medstock.view.route.DestinasiEntryResep

// --- IMPORT VIEWMODEL & PROVIDER ---
import com.example.medstock.viewmodel.DetailResepViewModel
import com.example.medstock.viewmodel.EditObatViewModel
import com.example.medstock.viewmodel.EntryResepViewModel
import com.example.medstock.viewmodel.EntryObatViewModel
import com.example.medstock.viewmodel.provider.PenyediaViewModel

@Composable
fun MedStockApp(navController: NavHostController = rememberNavController(), modifier: Modifier = Modifier){
    HostNavigasi(navController = navController, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostNavigasi(
    navController: NavHostController,
    modifier: Modifier = Modifier
){
    // START DESTINATION diubah ke Splash Screen (Bukan Home lagi)
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {

        // ==========================================
        // 1. BAGIAN AUTENTIKASI (Splash, Login, Register)
        // ==========================================

        // Splash Screen
        composable(Screen.Splash.route) {
            HalamanSplash(onTimeout = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        // Login Screen
        composable(Screen.Login.route) {
            HalamanLogin(
                onLoginSuccess = { role ->
                    if (role == "admin") {
                        navController.navigate(Screen.AdminHome.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.UserHome.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // Register Screen
        composable(Screen.Register.route) {
            HalamanRegister(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        // ==========================================
        // 2. DASHBOARD (User & Admin)
        // ==========================================

        // USER HOME (Apoteker Biasa) - Menggunakan HalamanHome.kt
        composable(Screen.UserHome.route){
            HalamanHome(
                navigateToEntryResep = { navController.navigate(DestinasiEntryResep.route) },
                navigateToEntryObat = { /* Access Denied */ },
                navigateToDetailResep = {
                    navController.navigate("${DestinasiDetailResep.route}/${it}")
                },
                navigateToEditObat = { /* Access Denied */ },

                // TAMBAHKAN LOGIKA LOGOUT DI SINI:
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) // Hapus semua riwayat halaman agar tidak bisa 'Back'
                    }
                }
            )
        }

        // ADMIN HOME (Full Akses) - Menggunakan HalamanAdminHome.kt
        composable(Screen.AdminHome.route) {
            HalamanAdminHome(
                navigateToEntryResep = { navController.navigate(DestinasiEntryResep.route) },
                navigateToEntryObat = { navController.navigate(DestinasiEntryObat.route) },
                navigateToDetailResep = {
                    navController.navigate("${DestinasiDetailResep.route}/${it}")
                },
                navigateToEditObat = {
                    navController.navigate("${DestinasiEditObat.route}/${it}")
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        // ==========================================
        // 3. FITUR CRUD (Resep & Obat) - KODE LAMA ANDA
        // ==========================================

        // ENTRY RESEP
        composable(DestinasiEntryResep.route){
            HalamanEntryResep(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                viewModel = viewModel<EntryResepViewModel>(factory = PenyediaViewModel.FactoryEntryResep)
            )
        }

        // DETAIL RESEP
        composable(route = DestinasiDetailResep.routeWithArgs,
            arguments = listOf(navArgument(resepIdArg) { type = NavType.IntType })
        ){ backStackEntry ->
            val resepId = backStackEntry.arguments?.getInt(resepIdArg) ?: 0
            HalamanDetailResep(
                navigateToEditResep = { navController.navigate("${DestinasiEditResep.route}/$it") },
                navigateBack = { navController.navigateUp() },
                viewModel = viewModel<DetailResepViewModel>(
                    factory = PenyediaViewModel.FactoryDetailResep(resepId = resepId)
                )
            )
        }

        // EDIT RESEP
        composable(route = DestinasiEditResep.routeWithArgs,
            arguments = listOf(navArgument(resepIdArg) { type = NavType.IntType })
        ){ backStackEntry ->
            val resepId = backStackEntry.arguments?.getInt(resepIdArg) ?: 0
            HalamanEditResep(
                navigateBack = {navController.popBackStack()},
                onNavigateUp = {navController.navigateUp()},
                viewModel = viewModel<DetailResepViewModel>(
                    factory = PenyediaViewModel.FactoryDetailResep(resepId = resepId)
                )
            )
        }

        // ENTRY OBAT
        composable(DestinasiEntryObat.route){
            HalamanEntryObat(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                viewModel = viewModel<EntryObatViewModel>(factory = PenyediaViewModel.FactoryEntryObat)
            )
        }

        // EDIT OBAT
        composable(route = DestinasiEditObat.routeWithArgs,
            arguments = listOf(navArgument(obatIdArg) { type = NavType.IntType })
        ){ backStackEntry ->
            val obatId = backStackEntry.arguments?.getInt(obatIdArg) ?: 0
            HalamanEditObat(
                navigateBack = {navController.popBackStack()},
                onNavigateUp = {navController.navigateUp()},
                viewModel = viewModel<EditObatViewModel>(
                    factory = PenyediaViewModel.FactoryEditObat(obatId = obatId)
                )
            )
        }
    }
}