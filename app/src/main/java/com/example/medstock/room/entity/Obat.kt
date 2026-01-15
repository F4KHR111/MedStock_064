package com.example.medstock.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "obat")
data class Obat(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val namaObat: String,
    val stok: Int,
    // Menggunakan Long (Timestamp) untuk tanggal kedaluwarsa lebih baik untuk perbandingan
    val tanggalKadaluarsa: Long,
    val harga: Int
)