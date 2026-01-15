package com.example.medstock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.medstock.ui.theme.MedStockTheme
// PERBAIKAN IMPORT: Panggil MedStockApp, bukan PetaNavigasi
import com.example.medstock.view.uicontroller.MedStockApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedStockTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // PANGGIL FUNGSI YANG BENAR:
                    MedStockApp()
                }
            }
        }
    }
}