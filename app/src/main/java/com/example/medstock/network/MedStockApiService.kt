package com.example.medstock.network

import retrofit2.http.Body
import retrofit2.http.POST

interface MedStockApiService {

    // Memanggil file login.php
    @POST("login.php")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // Memanggil file register.php
    @POST("register.php")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
}