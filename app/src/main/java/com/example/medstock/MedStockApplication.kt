package com.example.medstock

import android.app.Application
import com.example.medstock.repositori.AppContainer
import com.example.medstock.repositori.ContainerDataApp

class MedStockApplication : Application() {

    /**
     * AppContainer instance
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Menginisialisasi Container
        container = ContainerDataApp(this)
    }
}