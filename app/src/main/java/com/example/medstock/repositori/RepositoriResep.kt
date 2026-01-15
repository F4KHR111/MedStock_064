package com.example.medstock.repositori

import com.example.medstock.room.dao.ResepDao
import com.example.medstock.room.entity.Resep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface ResepRepository {
    fun getAllResepStream(): Flow<List<Resep>>
    suspend fun getAllResep(): List<Resep>
    suspend fun insertResep(resep: Resep)
    suspend fun updateResep(resep: Resep)
    fun getResepByDateRange(startOfDay: Long, endOfDay: Long): Flow<List<Resep>>

    // KOREKSI: Mengizinkan Resep bernilai null di Flow
    fun getResepStream(id: Int): Flow<Resep?> // <--- DIKOREKSI
    suspend fun getResepById(id: Int): Resep
    suspend fun deleteResep(resep: Resep)
}

class OfflineResepRepository(private val resepDao: ResepDao) : ResepRepository {

    // --- STREAMING FUNCTIONS (Tidak perlu withContext) ---
    override fun getAllResepStream(): Flow<List<Resep>> = resepDao.getAllResep()
    override fun getResepByDateRange(startOfDay: Long, endOfDay: Long): Flow<List<Resep>> {
        return resepDao.getResepByDateRange(startOfDay, endOfDay)
    }
    // Implementasi yang dikoreksi
    override fun getResepStream(id: Int): Flow<Resep?> = resepDao.getResepStream(id)

    // --- SUSPEND FUNCTIONS (WAJIB withContext(Dispatchers.IO) untuk Anti-Crash) ---

    override suspend fun getAllResep(): List<Resep> = withContext(Dispatchers.IO) {
        resepDao.getAllResepSuspend()
    }

    override suspend fun insertResep(resep: Resep) = withContext(Dispatchers.IO) {
        resepDao.insert(resep)
    }

    override suspend fun updateResep(resep: Resep) = withContext(Dispatchers.IO) {
        resepDao.update(resep)
    }

    override suspend fun getResepById(id: Int): Resep = withContext(Dispatchers.IO) {
        resepDao.getResepById(id)
    }

    override suspend fun deleteResep(resep: Resep) = withContext(Dispatchers.IO) {
        resepDao.delete(resep)
    }
}