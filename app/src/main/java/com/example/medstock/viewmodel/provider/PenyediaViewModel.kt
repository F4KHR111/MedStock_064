package com.example.medstock.viewmodel.provider

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.medstock.MedStockApplication
import com.example.medstock.viewmodel.DetailResepViewModel
import com.example.medstock.viewmodel.EditObatViewModel
import com.example.medstock.viewmodel.EntryObatViewModel
import com.example.medstock.viewmodel.EntryResepViewModel
import com.example.medstock.viewmodel.HomeViewModel

/**
 * Mendapatkan instance [AppContainer] dari [Application] yang berjalan,
 * kemudian membuat factory untuk ViewModels yang membutuhkan Repository.
 */
object PenyediaViewModel {

    // Factory untuk HomeViewModel (Butuh Obat & Resep Repository)
    val FactoryHome = viewModelFactory {
        initializer {
            // Akses container melalui MedStockApplication
            val container = aplikasiMedStock().container
            HomeViewModel(
                obatRepository = container.obatRepository,
                resepRepository = container.resepRepository
            )
        }
    }

    // Factory untuk EntryResepViewModel (Butuh Obat & Resep Repository)
    val FactoryEntryResep = viewModelFactory {
        initializer {
            val container = aplikasiMedStock().container
            EntryResepViewModel(
                resepRepository = container.resepRepository,
                obatRepository = container.obatRepository
            )
        }
    }

    // Factory untuk EntryObatViewModel (Butuh Obat Repository)
    val FactoryEntryObat = viewModelFactory {
        initializer {
            val container = aplikasiMedStock().container
            EntryObatViewModel(container.obatRepository)
        }
    }

    // Factory untuk DetailResepViewModel (Butuh Resep Repository, Obat Repository, dan ID Resep)
    fun FactoryDetailResep(resepId: Int): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val container = aplikasiMedStock().container
            DetailResepViewModel(
                resepId = resepId,
                resepRepository = container.resepRepository,
                // REVISI KRITIS: Tambahkan ObatRepository untuk logika delete/kembalikan stok
                obatRepository = container.obatRepository
            )
        }
    }

    // Factory untuk EditObatViewModel (Butuh Obat Repository, dan ID Obat)
    fun FactoryEditObat(obatId: Int): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val container = aplikasiMedStock().container
            EditObatViewModel(
                obatId = obatId,
                obatRepository = container.obatRepository
            )
        }
    }
}

/**
 * Extension function untuk mendapatkan instance MedStockApplication.
 */
fun CreationExtras.aplikasiMedStock(): MedStockApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MedStockApplication)