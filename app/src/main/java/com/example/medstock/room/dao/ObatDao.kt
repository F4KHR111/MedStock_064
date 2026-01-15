package com.example.medstock.room.dao

import androidx.room.*
import com.example.medstock.room.entity.Obat
import kotlinx.coroutines.flow.Flow

@Dao
interface ObatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(obat: Obat)

    @Update
    suspend fun update(obat: Obat)

    @Delete
    suspend fun delete(obat: Obat)

    @Query("SELECT * FROM obat ORDER BY namaObat ASC")
    fun getAllObat(): Flow<List<Obat>>

    // KOREKSI: Mengubah tipe kembalian menjadi Flow<Obat?>
    @Query("SELECT * FROM obat WHERE id = :id")
    fun getObatStream(id: Int): Flow<Obat?> // <-- DIKOREKSI

    // Fungsi getObat suspend tetap dipertahankan untuk operasi satu kali (seperti delete)
    @Query("SELECT * FROM obat WHERE id = :id")
    suspend fun getObat(id: Int): Obat

    // Query untuk mengurangi stok saat resep diberikan
    @Query("UPDATE obat SET stok = stok - :jumlah WHERE id = :obatId") // Gunakan '+' karena fungsi kurangiStokObat di Repository mengirim nilai negatif/positif
    suspend fun kurangiStok(obatId: Int, jumlah: Int)
}