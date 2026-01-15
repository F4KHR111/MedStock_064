package com.example.medstock.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resep")
data class Resep(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val namaPasien: String,
    // Menggunakan Long (Timestamp) untuk tanggal resep
    val tanggal: Long,
    val obatId: Int,
    val namaObat: String,
    val jumlah: Int,
    val keteranganDokter: String?
)