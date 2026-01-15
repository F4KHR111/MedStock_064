package com.example.medstock.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Import tambahan untuk API
import com.example.medstock.network.RegisterRequest
import com.example.medstock.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun HalamanRegister(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    // State Data Form
    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // State UI (Loading & Error)
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Scope untuk Coroutine
    val scope = rememberCoroutineScope()

    val blueGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF64B5F6), Color(0xFF1976D2))
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Header Biru
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(blueGradient),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = (-20).dp)
            ) {
                Text(
                    text = "MedStock",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // 2. Form Register
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 180.dp),
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Silahkan isi data dengan lengkap",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Input Nama
                OutlinedTextField(
                    value = nama,
                    onValueChange = {
                        nama = it
                        errorMessage = ""
                    },
                    label = { Text("Nama Lengkap") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Input Email
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = ""
                    },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Input Password
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = ""
                    },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Menampilkan Pesan Error
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tombol Sign Up dengan Integrasi API
                Button(
                    onClick = {
                        // 1. Validasi Input
                        if (nama.isBlank() || email.isBlank() || password.isBlank()) {
                            errorMessage = "Semua data wajib diisi!"
                            return@Button
                        }

                        // 2. Mulai Loading
                        isLoading = true
                        errorMessage = ""

                        // 3. Panggil API Register
                        scope.launch {
                            try {
                                val request = RegisterRequest(nama, email, password)
                                val response = RetrofitClient.instance.register(request)

                                if (response.status) {
                                    // Berhasil -> Kembali ke Login
                                    onRegisterSuccess()
                                } else {
                                    // Gagal (misal email sudah ada)
                                    errorMessage = response.message
                                }
                            } catch (e: Exception) {
                                errorMessage = "Gagal terhubung: ${e.localizedMessage}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                    enabled = !isLoading // Cegah klik ganda saat loading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Sign Up", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Text("Sudah punya akun? ", color = Color.Gray)
                    Text(
                        text = "Sign In",
                        color = Color(0xFF4285F4),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            if (!isLoading) onLoginClick()
                        }
                    )
                }
            }
        }
    }
}