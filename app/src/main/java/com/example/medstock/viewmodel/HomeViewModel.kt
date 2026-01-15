package com.example.medstock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medstock.repositori.ObatRepository
import com.example.medstock.repositori.ResepRepository
import com.example.medstock.room.entity.Obat
import com.example.medstock.room.entity.Resep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import java.util.concurrent.TimeUnit

data class HomeUiState(
    val daftarObat: List<Obat> = emptyList(),
    val daftarResep: List<Resep> = emptyList(),
    val stokRendahAlert: List<Obat> = emptyList(),
    val expiredAlert: List<Obat> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val filterDate: Long? = null
)

class HomeViewModel(
    private val obatRepository: ObatRepository,
    private val resepRepository: ResepRepository
) : ViewModel() {

    private val STOK_RENDAH_TRESHOLD = 10
    private val EXPIRED_TRESHOLD_DAYS = 30
    private val MS_IN_DAY = TimeUnit.DAYS.toMillis(1)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // State untuk data Resep yang akan dipreview/ekspor
    private val _resepToExport = MutableStateFlow<List<Resep>>(emptyList())
    val resepToExport: StateFlow<List<Resep>> = _resepToExport.asStateFlow() // Dipakai di UI untuk Preview

    // KOREKSI: Menggunakan nama fungsi yang benar untuk Flow di ObatRepository
    private val allObatFlow = obatRepository.getAllObatStream()
    private val allResepFlow = resepRepository.getAllResepStream()

    val uiState: StateFlow<HomeUiState> = combine(
        allObatFlow,
        allResepFlow,
        _searchQuery
    ) { daftarObat: List<Obat>, listResep: List<Resep>, query: String ->

        val filteredResep = if (query.isBlank()) {
            listResep
        } else {
            listResep.filter { resep ->
                resep.namaPasien.contains(query, ignoreCase = true) ||
                        resep.namaObat.contains(query, ignoreCase = true)
            }
        }

        val batasWaktuKadaluarsa = System.currentTimeMillis() + (EXPIRED_TRESHOLD_DAYS * MS_IN_DAY)
        val akanKadaluarsa = daftarObat.filter { it.tanggalKadaluarsa < batasWaktuKadaluarsa }
        val stokRendah = daftarObat.filter { it.stok <= STOK_RENDAH_TRESHOLD }

        HomeUiState(
            daftarObat = daftarObat,
            daftarResep = filteredResep,
            stokRendahAlert = stokRendah,
            expiredAlert = akanKadaluarsa,
            searchQuery = query,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = HomeUiState(isLoading = true)
    )

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    // BARU: Fungsi untuk memuat data Resep ke State Preview
    fun loadResepForExport() {
        viewModelScope.launch {
            // Mengambil SEMUA resep yang ada di DB (menggunakan suspend fun)
            val listResep = resepRepository.getAllResep()
            _resepToExport.update { listResep }
        }
    }

    // BARU: Fungsi untuk membersihkan state preview
    fun clearResepExportState() {
        _resepToExport.update { emptyList() }
    }

    // Fungsi Export PDF (diasumsikan logic pembuatan file ada di luar VM)
    fun exportResepToPdf() {
        viewModelScope.launch {
            val listResep = _resepToExport.value
            if (listResep.isNotEmpty()) {
                // TODO: Panggil fungsi utility untuk membuat dan menyimpan PDF di sini
                // Contoh: PdfExportUtility.export(listResep)

                // Setelah selesai, bersihkan state
                clearResepExportState()
            }
        }
    }
}