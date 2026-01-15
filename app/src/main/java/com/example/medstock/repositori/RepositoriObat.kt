package com.example.medstock.repositori

import com.example.medstock.room.dao.ObatDao
import com.example.medstock.room.entity.Obat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface ObatRepository {
    fun getAllObatStream(): Flow<List<Obat>> // Menggunakan nama yang benar di HomeViewModel
    // KOREKSI: Mengizinkan Obat bernilai null di Flow
    fun getObatStream(id: Int): Flow<Obat?> // <-- DIKOREKSI
    suspend fun getObat(id: Int): Obat
    suspend fun insertObat(obat: Obat)
    suspend fun updateObat(obat: Obat)
    suspend fun deleteObat(obat: Obat)
    // Diubah namanya agar konsisten (meskipun fungsionalitasnya sama)
    suspend fun kurangiStokObat(obatId: Int, jumlah: Int)
}

class OfflineObatRepository(private val obatDao: ObatDao) : ObatRepository {

    // --- STREAMING FUNCTIONS (Tidak perlu withContext) ---
    override fun getAllObatStream(): Flow<List<Obat>> = obatDao.getAllObat()
    // KOREKSI: Implementasi Flow<Obat?>
    override fun getObatStream(id: Int): Flow<Obat?> = obatDao.getObatStream(id)

    // --- SUSPEND FUNCTIONS (WAJIB withContext(Dispatchers.IO) untuk Anti-Crash) ---

    override suspend fun getObat(id: Int): Obat = withContext(Dispatchers.IO) {
        obatDao.getObat(id)
    }

    override suspend fun insertObat(obat: Obat) = withContext(Dispatchers.IO) {
        obatDao.insert(obat)
    }

    override suspend fun updateObat(obat: Obat) = withContext(Dispatchers.IO) {
        obatDao.update(obat)
    }

    override suspend fun deleteObat(obat: Obat) = withContext(Dispatchers.IO) {
        obatDao.delete(obat) // Kunci stabilitas saat hapus
    }

    override suspend fun kurangiStokObat(obatId: Int, jumlah: Int) = withContext(Dispatchers.IO) {
        // Fungsi ini memanggil DAO kurangiStok yang sudah menggunakan query UPDATE yang aman
        obatDao.kurangiStok(obatId, jumlah)
    }
}