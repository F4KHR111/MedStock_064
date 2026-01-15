package com.example.medstock.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medstock.repositori.ObatRepository
import com.example.medstock.room.entity.Obat
import kotlinx.coroutines.launch
import java.util.Date

// Data class untuk menampung State input form Obat (Dipindahkan/Didefinisikan di sini)
data class ObatDetails(
    val id: Int = 0,
    val namaObat: String = "",
    val stok: String = "",
    val tanggalKadaluarsaInput: String = "",
    val harga: String = ""
)

data class ObatEntryUiState(
    val obatDetails: ObatDetails = ObatDetails(),
    val isEntryValid: Boolean = false
)

class EntryObatViewModel(private val obatRepository: ObatRepository) : ViewModel() {

    var obatUiState by mutableStateOf(ObatEntryUiState())
        private set

    // Fungsi untuk memperbarui state saat user mengetik
    fun updateUiState(obatDetails: ObatDetails) {
        obatUiState = ObatEntryUiState(
            obatDetails = obatDetails,
            isEntryValid = validateInput(obatDetails)
        )
    }

    // Fungsi untuk menyimpan Obat baru
    suspend fun saveObat() {
        if (obatUiState.isEntryValid) {
            obatRepository.insertObat(obatUiState.obatDetails.toObat())
        }
    }

    // Validasi input
    private fun validateInput(uiState: ObatDetails): Boolean {
        return with(uiState) {
            namaObat.isNotBlank() && stok.toIntOrNull() != null && stok.toIntOrNull()!! >= 0 &&
                    tanggalKadaluarsaInput.isNotBlank() && harga.toIntOrNull() != null && harga.toIntOrNull()!! >= 0
        }
    }
}

// Konversi Data Class (Extension Functions)

// Konversi dari UI Details ke Room Entity
fun ObatDetails.toObat(): Obat = Obat(
    id = id,
    namaObat = namaObat,
    stok = stok.toIntOrNull() ?: 0,
    // CATATAN: Konversi tanggal String ke Long/Timestamp dilakukan di sini
    tanggalKadaluarsa = Date().time + 8640000000L, // Placeholder
    harga = harga.toIntOrNull() ?: 0
)

// Konversi dari Room Entity ke UI Details
fun Obat.toObatDetails(): ObatDetails = ObatDetails(
    id = id,
    namaObat = namaObat,
    stok = stok.toString(),
    // Placeholder, perlu logika konversi Long ke String
    tanggalKadaluarsaInput = "Convert Long to String Date",
    harga = harga.toString()
)