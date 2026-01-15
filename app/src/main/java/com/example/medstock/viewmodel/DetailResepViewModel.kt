package com.example.medstock.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medstock.repositori.ObatRepository
import com.example.medstock.repositori.ResepRepository
import com.example.medstock.room.entity.Resep
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class ResepDetailUiState(
    val resepDetails: ResepDetails = ResepDetails(),
    val isEntryValid: Boolean = false
)

class DetailResepViewModel(
    private val resepRepository: ResepRepository,
    private val obatRepository: ObatRepository,
    private val resepId: Int
) : ViewModel() {

    var resepUiState: ResepDetailUiState by mutableStateOf(ResepDetailUiState())
        private set;

    // 1. Logika untuk fetch detail resep berdasarkan resepId (Read)
    val uiState: StateFlow<ResepDetailUiState> = resepRepository.getResepStream(resepId)
        // KOREKSI: Hapus filterNotNull dan tangani Resep?
        .map { resep: Resep? ->
            if (resep != null) {
                resep.toResepDetailUiState(resepUiState.isEntryValid)
            } else {
                // Saat resep null (data dihapus), kembalikan state kosong/default
                ResepDetailUiState()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ResepDetailUiState(resepDetails = ResepDetails(id = resepId)) // Optional: Set ID awal untuk Detail
        )

    init {
        // Sinkronisasi Flow ke mutable state untuk form editing
        viewModelScope.launch {
            uiState.collect {
                // Sinkronisasi hanya jika ID resep tidak berubah (atau jika itu adalah nilai yang sah)
                if (it.resepDetails.id > 0) {
                    resepUiState = it
                }
            }
        }
    }

    fun updateUiState(resepDetails: ResepDetails) {
        resepUiState = resepUiState.copy(
            resepDetails = resepDetails,
            isEntryValid = validateInput(resepDetails)
        )
    }

    fun updateResep() {
        if (resepUiState.isEntryValid) {
            viewModelScope.launch {
                val resepEntity = resepUiState.resepDetails.toResep()
                resepRepository.updateResep(resepEntity)
            }
        }
    }

    // 3. Logika untuk Delete Resep (Anti-Crash dan Rollback Stok)
    fun deleteResep() {
        viewModelScope.launch {
            try {
                // Operasi 1: Ambil Resep (sudah thread-safe di Repository)
                val resepToDelete = resepRepository.getResepById(resepId)

                // Operasi 2: Rollback Stok
                val jumlahResep = resepToDelete.jumlah
                val obatIdResep = resepToDelete.obatId

                if (obatIdResep > 0 && jumlahResep > 0) {
                    // Panggil Repository Obat (sudah thread-safe)
                    obatRepository.kurangiStokObat(
                        obatId = obatIdResep,
                        jumlah = -jumlahResep
                    )
                }

                // Operasi 3: Hapus Resep
                resepRepository.deleteResep(resepToDelete)
            } catch (e: Exception) {
                // Mencatat Error untuk dianalisis (Logcat)
                android.util.Log.e("DetailVM", "Error deleting resep (ID: $resepId): ${e.message}", e)
                // Jika masih crash setelah semua perbaikan, log ini akan sangat membantu.
            }
        }
    }

    private fun validateInput(uiState: ResepDetails): Boolean {
        return uiState.namaPasien.isNotBlank() && uiState.jumlah.toIntOrNull() != null && (uiState.jumlah.toIntOrNull() ?: 0) > 0
    }
}

// --- Extension Functions (Tanpa Perubahan Logika) ---
// ... (Long.formatDate, Resep.toResepDetails, Resep.toResepDetailUiState, ResepDetails.toResep) ...
private fun Long.formatDate(format: String = "dd MMMM yyyy"): String {
    return SimpleDateFormat(format, Locale("id", "ID")).format(Date(this))
}

fun Resep.toResepDetails(): ResepDetails = ResepDetails(
    id = id,
    namaPasien = namaPasien,
    tanggalInput = tanggal.formatDate("yyyy-MM-dd"),
    tanggalDisplay = tanggal.formatDate(),
    obatId = obatId.toString(),
    namaObat = namaObat,
    jumlah = jumlah.toString(),
    keteranganDokter = keteranganDokter ?: ""
)

fun Resep.toResepDetailUiState(isEntryValid: Boolean = false): ResepDetailUiState = ResepDetailUiState(
    resepDetails = this.toResepDetails(),
    isEntryValid = isEntryValid
)

fun ResepDetails.toResep(): Resep {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val tanggalLong = try { dateFormat.parse(tanggalInput)?.time ?: Date().time } catch (e: Exception) { Date().time }

    return Resep(
        id = id,
        namaPasien = namaPasien,
        tanggal = tanggalLong,
        obatId = obatId.toIntOrNull() ?: 0,
        namaObat = namaObat,
        jumlah = jumlah.toIntOrNull() ?: 0,
        keteranganDokter = keteranganDokter
    )
}