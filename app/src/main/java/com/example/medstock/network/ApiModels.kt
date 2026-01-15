package com.example.medstock.network

// Apa yang kita kirim saat Login
data class LoginRequest(
    val email: String,
    val password: String
)

// Apa yang kita terima setelah Login
data class LoginResponse(
    val status: Boolean,
    val message: String,
    val role: String? = null, // Bisa null jika login gagal
    val nama: String? = null
)

// Apa yang kita kirim saat Register
data class RegisterRequest(
    val nama: String,
    val email: String,
    val password: String
)

// Apa yang kita terima setelah Register
data class RegisterResponse(
    val status: Boolean,
    val message: String
)