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
// Import tambahan untuk API dan Coroutine
import com.example.medstock.network.LoginRequest
import com.example.medstock.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun HalamanLogin(
    onLoginSuccess: (String) -> Unit,
    onRegisterClick: () -> Unit
) {
    // Variable State
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // State baru untuk Loading
    var isLoading by remember { mutableStateOf(false) }

    // Scope untuk menjalankan proses background (API)
    val scope = rememberCoroutineScope()

    val blueGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF64B5F6), Color(0xFF1976D2))
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. HEADER BIRU
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(blueGradient),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = (-40).dp)
            ) {
                Text(
                    text = "MedStock",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // 2. FORM LOGIN
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 220.dp),
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
                    text = "Welcome Back",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Silahkan masukan email dan password",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Input Email
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = ""
                    },
                    label = { Text("Email") },
                    isError = errorMessage.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Input Password
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = ""
                    },
                    label = { Text("Password") },
                    isError = errorMessage.isNotEmpty(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Pesan Error
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp).align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- TOMBOL SIGN IN (DENGAN INTEGRASI API) ---
                Button(
                    onClick = {
                        // 1. Validasi Input Kosong
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Email dan Password tidak boleh kosong!"
                            return@Button
                        }

                        // 2. Mulai Loading
                        isLoading = true
                        errorMessage = ""

                        // 3. Panggil API di Background
                        scope.launch {
                            try {
                                val request = LoginRequest(email, password)
                                val response = RetrofitClient.instance.login(request)

                                if (response.status) {
                                    // LOGIN SUKSES
                                    // Ambil role dari response PHP ("admin" atau "user")
                                    val role = response.role ?: "user"
                                    onLoginSuccess(role)
                                } else {
                                    // LOGIN GAGAL (Password salah / User tidak ditemukan)
                                    errorMessage = response.message
                                }
                            } catch (e: Exception) {
                                // ERROR KONEKSI (Server mati, beda wifi, dll)
                                errorMessage = "Gagal terhubung: ${e.localizedMessage}"
                            } finally {
                                // Selesai Loading
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                    enabled = !isLoading // Matikan tombol saat loading
                ) {
                    if (isLoading) {
                        // Tampilkan putaran loading jika sedang memanggil API
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Sign In", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Text("Belum punya akun? ", color = Color.Gray)
                    Text(
                        text = "Register",
                        color = Color(0xFF4285F4),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            if (!isLoading) onRegisterClick()
                        }
                    )
                }
            }
        }
    }
}