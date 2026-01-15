package com.example.medstock.room.dao

import androidx.room.*
import com.example.medstock.room.entity.Resep
import kotlinx.coroutines.flow.Flow

@Dao
interface ResepDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(resep: Resep)

    @Update
    suspend fun update(resep: Resep)

    @Delete
    suspend fun delete(resep: Resep)

    // Query untuk mengambil Resep (Entity) berdasarkan ID
    @Query("SELECT * FROM resep WHERE id = :id")
    suspend fun getResepById(id: Int): Resep

    // KOREKSI: Mengubah tipe kembalian menjadi Flow<Resep?> untuk mengatasi crash saat dihapus
    @Query("SELECT * FROM resep WHERE id = :id")
    fun getResepStream(id: Int): Flow<Resep?> // <--- DIKOREKSI

    @Query("SELECT * FROM resep ORDER BY tanggal DESC")
    fun getAllResep(): Flow<List<Resep>>

    // Fungsi suspend untuk mengambil semua resep (digunakan untuk rekap/PDF)
    @Query("SELECT * FROM resep ORDER BY tanggal DESC")
    suspend fun getAllResepSuspend(): List<Resep>

    @Query("SELECT * FROM resep WHERE tanggal >= :startOfDay AND tanggal < :endOfDay ORDER BY tanggal DESC")
    fun getResepByDateRange(startOfDay: Long, endOfDay: Long): Flow<List<Resep>>
}