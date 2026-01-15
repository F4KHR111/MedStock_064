package com.example.medstock.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medstock.repositori.ObatRepository
import com.example.medstock.repositori.ResepRepository
import com.example.medstock.room.entity.Obat
import com.example.medstock.room.entity.Resep
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale // Wajib diimpor

data class ResepDetails(
    val id: Int = 0,
    val namaPasien: String = "",
    val tanggalInput: String = "",
    val tanggalDisplay: String = "", // Untuk tampilan di Detail
    val obatId: String = "",
    val namaObat: String = "",
    val jumlah: String = "",
    val keteranganDokter: String = ""
)

data class ResepEntryUiState(
    val resepDetails: ResepDetails = ResepDetails(),
    val daftarObatDropdown: List<Obat> = emptyList(),
    val isEntryValid: Boolean = false
)

class EntryResepViewModel(
    private val resepRepository: ResepRepository,
    private val obatRepository: ObatRepository
) : ViewModel() {

    var resepUiState by mutableStateOf(ResepEntryUiState())
        private set

    // Ambil daftar obat untuk dropdown (pilihan obat)
    private val obatList: StateFlow<List<Obat>> = obatRepository.getAllObatStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    init {
        // Gabungkan list obat ke UI State
        viewModelScope.launch {
            obatList.collect { obat ->
                resepUiState = resepUiState.copy(daftarObatDropdown = obat)
            }
        }
    }

    // Fungsi untuk memperbarui state saat user mengetik
    fun updateUiState(resepDetails: ResepDetails) {
        resepUiState = resepUiState.copy(
            resepDetails = resepDetails,
            isEntryValid = validateInput(resepDetails)
        )
    }

    // KOREKSI: saveResep menjadi non-suspend, logika dijalankan di dalam viewModelScope
    fun saveResep() {
        if (!resepUiState.isEntryValid) return

        viewModelScope.launch { // <--- Eksekusi di background thread
            val details = resepUiState.resepDetails
            val jumlahDiminta = details.jumlah.toIntOrNull() ?: 0
            val obatIdDipilih = details.obatId.toIntOrNull() ?: 0

            val obatDipilih = obatList.value.find { it.id == obatIdDipilih }

            if (obatDipilih == null || jumlahDiminta > obatDipilih.stok) {
                // Stok tidak mencukupi atau obat tidak ditemukan
                // TODO: Tambahkan logic untuk menampilkan error ke user jika diperlukan
                return@launch // Keluar dari coroutine
            }

            // 1. Simpan Resep
            val newResep = details.toResep(obatDipilih.namaObat)
            resepRepository.insertResep(newResep)

            // 2. Update Stok Obat (Kurangi)
            // jumlahDiminta adalah nilai positif, Repository akan mengurangi stok.
            obatRepository.kurangiStokObat(obatIdDipilih, jumlahDiminta)
        }
    }

    private fun validateInput(uiState: ResepDetails): Boolean {
        // Logika validasi
        return with(uiState) {
            namaPasien.isNotBlank() && tanggalInput.isNotBlank() &&
                    obatId.isNotBlank() && jumlah.toIntOrNull() != null && jumlah.toIntOrNull()!! > 0
        }
    }
}

// Konversi Data Class
// KOREKSI: Tambahkan SimpleDateFormat untuk parsing tanggal di toResep
fun ResepDetails.toResep(namaObat: String): Resep {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val tanggalLong = try {
        dateFormat.parse(tanggalInput)?.time ?: Date().time
    } catch (e: Exception) {
        Date().time
    }

    return Resep(
        namaPasien = namaPasien,
        tanggal = tanggalLong,
        obatId = obatId.toIntOrNull() ?: 0,
        namaObat = namaObat,
        jumlah = jumlah.toIntOrNull() ?: 0,
        keteranganDokter = keteranganDokter
    )
}