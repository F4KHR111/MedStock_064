package com.example.medstock.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medstock.R
import kotlinx.coroutines.delay

@Composable
fun HalamanSplash(onTimeout: () -> Unit) {
    // Timer 2 detik sebelum pindah ke Login
    LaunchedEffect(true) {
        delay(2000)
        onTimeout()
    }

    // UI: Logo di tengah layar (Sesuai Artboard 1)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pastikan kamu punya drawable logo (misal: ic_logo atau logo_medstock)
        // Jika belum ada, gunakan Icon default dulu
        Image(
            painter = painterResource(id = R.drawable.logo_medstock), // Ganti dengan R.drawable.logo_kamu
            contentDescription = "Logo MedStock",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "MedStock",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4285F4) // Warna Biru MedStock
        )
    }
}