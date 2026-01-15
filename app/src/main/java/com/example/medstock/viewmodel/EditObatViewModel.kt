package com.example.medstock.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medstock.repositori.ObatRepository
import com.example.medstock.room.entity.Obat
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
// import kotlinx.coroutines.flow.filterNotNull // <-- HAPUS INI
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class ObatDetailUiState(
    val obatDetails: ObatDetails = ObatDetails(),
    val isEntryValid: Boolean = false
)

class EditObatViewModel(
    private val obatRepository: ObatRepository,
    private val obatId: Int
) : ViewModel() {

    var obatUiState: ObatDetailUiState by mutableStateOf(ObatDetailUiState())
        private set

    // MENGAMBIL DATA AWAL DARI DB
    val uiState: StateFlow<ObatDetailUiState> = obatRepository.getObatStream(obatId) // Flow<Obat?>
        // KOREKSI: Hapus filterNotNull dan tangani null secara manual
        .map { obat: Obat? ->
            if (obat != null) {
                // Jika data ada, konversi ke UI State
                obat.toObatDetailUiState(obatUiState.isEntryValid)
            } else {
                // Jika data null (dihapus), kembalikan state kosong untuk menghindari crash
                ObatDetailUiState()
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ObatDetailUiState()
        )

    init {
        viewModelScope.launch {
            uiState.collect {
                if (it.obatDetails.id > 0) {
                    obatUiState = it
                }
            }
        }
    }

    fun updateUiState(obatDetails: ObatDetails) {
        obatUiState = obatUiState.copy(
            obatDetails = obatDetails,
            isEntryValid = validateInput(obatDetails)
        )
    }

    // UPDATE: Fungsi non-suspend
    fun updateObat() {
        if (obatUiState.isEntryValid) {
            viewModelScope.launch {
                val updatedObat = obatUiState.obatDetails.toObat()
                obatRepository.updateObat(updatedObat)
            }
        }
    }

    // DELETE: Fungsi non-suspend
    fun deleteObat() {
        viewModelScope.launch {
            try {
                val obatToDelete = obatUiState.obatDetails.toObat()
                obatRepository.deleteObat(obatToDelete)
            } catch (e: Exception) {
                android.util.Log.e("EditObatVM", "Error deleting obat (ID: $obatId): ${e.message}", e)
            }
        }
    }

    private fun validateInput(uiState: ObatDetails): Boolean {
        return with(uiState) {
            namaObat.isNotBlank() &&
                    stok.toIntOrNull() != null && stok.toInt() >= 0 &&
                    harga.toIntOrNull() != null && harga.toInt() >= 0 &&
                    tanggalKadaluarsaInput.isNotBlank()
        }
    }

    // --- Extension Functions (Dibuat private untuk membatasi scope & menghindari ambiguitas) ---

    private fun Long.formatDate(format: String = "dd MMMM yyyy"): String {
        return SimpleDateFormat(format, Locale("id", "ID")).format(Date(this))
    }

    private fun Obat.toObatDetails(): ObatDetails {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val tanggalString = dateFormat.format(Date(tanggalKadaluarsa))

        return ObatDetails(
            id = id,
            namaObat = namaObat,
            stok = stok.toString(),
            harga = harga.toString(),
            tanggalKadaluarsaInput = tanggalString
        )
    }

    private fun Obat.toObatDetailUiState(isEntryValid: Boolean = false): ObatDetailUiState = ObatDetailUiState(
        obatDetails = this.toObatDetails(),
        isEntryValid = isEntryValid
    )

    private fun ObatDetails.toObat(): Obat {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val tanggalLong = try {
            dateFormat.parse(tanggalKadaluarsaInput)?.time ?: Date().time
        } catch (e: Exception) {
            Date().time
        }

        return Obat(
            id = id,
            namaObat = namaObat,
            stok = stok.toIntOrNull() ?: 0,
            harga = harga.toIntOrNull() ?: 0,
            tanggalKadaluarsa = tanggalLong
        )
    }
}