package com.example.medstock.repositori

import android.content.Context
import com.example.medstock.room.MedStockDatabase

/**
 * Interface untuk menyediakan dependencies (Repository)
 */
interface AppContainer {
    // Menyediakan instance untuk Repository Obat dan Resep
    val obatRepository: ObatRepository
    val resepRepository: ResepRepository
}

/**
 * Implementasi konkret dari AppContainer
 */
class ContainerDataApp(private val context: Context): AppContainer {

    // Inisialisasi Database (Singleton)
    private val database: MedStockDatabase by lazy {
        MedStockDatabase.getDatabase(context)
    }

    // Menyediakan instance ObatRepository
    override val obatRepository: ObatRepository by lazy {
        OfflineObatRepository(database.obatDao())
    }

    // Menyediakan instance ResepRepository
    override val resepRepository: ResepRepository by lazy {
        OfflineResepRepository(database.resepDao())
    }
}