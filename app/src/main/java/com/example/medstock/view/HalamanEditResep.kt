package com.example.medstock.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medstock.R
import com.example.medstock.viewmodel.DetailResepViewModel
import com.example.medstock.viewmodel.EntryResepViewModel // Import ini
import com.example.medstock.viewmodel.ResepDetails
import com.example.medstock.viewmodel.provider.PenyediaViewModel
import com.example.medstock.room.entity.Obat
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEditResep(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailResepViewModel = viewModel(factory = PenyediaViewModel.FactoryDetailResep(0)),
    // Tambahkan EntryResepViewModel untuk meminjam daftar obat (Dropdown)
    viewModelEntry: EntryResepViewModel = viewModel(factory = PenyediaViewModel.FactoryEntryResep)
) {
    val coroutineScope = rememberCoroutineScope()
    val resepUiState = viewModel.resepUiState

    // Ambil daftar obat dari viewModelEntry untuk mengisi dropdown
    val daftarObat = viewModelEntry.resepUiState.daftarObatDropdown

    // --- STRUKTUR UI (Blue Header + White Card) ---
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 1. Header Biru
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color(0xFF4C4CFF))
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // 2. Custom Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateUp,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Edit Data Resep",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Card Putih
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                EditResepBody(
                    resepUiState = resepUiState.resepDetails,
                    daftarObat = daftarObat, // Masukkan daftar obat ke sini
                    onResepValueChange = viewModel::updateUiState,
                    onUpdateClick = {
                        coroutineScope.launch {
                            viewModel.updateResep()
                            navigateBack()
                        }
                    },
                    isEntryValid = resepUiState.isEntryValid,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    }
}

@Composable
fun EditResepBody(
    resepUiState: ResepDetails,
    daftarObat: List<Obat>, // Parameter ini sekarang berisi data
    onResepValueChange: (ResepDetails) -> Unit,
    onUpdateClick: () -> Unit,
    isEntryValid: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = "Formulir Edit Resep",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF4C4CFF),
            fontWeight = FontWeight.Bold
        )

        ResepInputFormEdit(
            resepDetails = resepUiState,
            daftarObat = daftarObat,
            onValueChange = onResepValueChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onUpdateClick,
            enabled = isEntryValid,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C4CFF)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.Filled.Save, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.btn_update), fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResepInputFormEdit(
    resepDetails: ResepDetails,
    daftarObat: List<Obat>,
    modifier: Modifier = Modifier,
    onValueChange: (ResepDetails) -> Unit = {}
) {
    // State Dropdown & Date Picker
    var expandedDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // 1. Nama Pasien
        OutlinedTextField(
            value = resepDetails.namaPasien,
            onValueChange = { onValueChange(resepDetails.copy(namaPasien = it)) },
            label = { Text(stringResource(R.string.nama_pasien)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        // 2. Tanggal (Date Picker)
        OutlinedTextField(
            value = resepDetails.tanggalInput,
            onValueChange = { },
            label = { Text(stringResource(R.string.tanggal)) },
            placeholder = { Text("YYYY-MM-DD") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            readOnly = true,
            enabled = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Pilih Tanggal")
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )

        // 3. Obat (SEKARANG DROPDOWN - Sama seperti Input)
        Box {
            // Menampilkan nama obat saat ini. Jika ID obat cocok dengan daftar, ambil namanya.
            // Jika tidak (misal obat dihapus), gunakan nama yang tersimpan di resep.
            val selectedObatName = daftarObat.find { it.id == resepDetails.obatId.toIntOrNull() }?.namaObat
                ?: resepDetails.namaObat

            OutlinedTextField(
                value = selectedObatName,
                onValueChange = {}, // ReadOnly karena dropdown
                label = { Text(stringResource(R.string.obat_diberikan)) },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.clickable { expandedDropdown = true }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedDropdown = true },
                shape = RoundedCornerShape(12.dp)
            )

            DropdownMenu(
                expanded = expandedDropdown,
                onDismissRequest = { expandedDropdown = false },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .background(Color.White)
            ) {
                daftarObat.forEach { obat ->
                    val stokTersedia = stringResource(R.string.info_stok_tersedia, obat.stok.toString())
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(obat.namaObat, fontWeight = FontWeight.Bold)
                                Text(stokTersedia, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        },
                        onClick = {
                            // Update ID Obat dan Nama Obat di state
                            // Penting: Update nama juga agar tampilan langsung berubah
                            onValueChange(resepDetails.copy(
                                obatId = obat.id.toString(),
                                namaObat = obat.namaObat
                            ))
                            expandedDropdown = false
                        }
                    )
                    Divider(color = Color.LightGray.copy(alpha = 0.5f))
                }
            }
        }

        // 4. Jumlah Unit
        OutlinedTextField(
            value = resepDetails.jumlah,
            onValueChange = {
                if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                    onValueChange(resepDetails.copy(jumlah = it))
                }
            },
            label = { Text(stringResource(R.string.jumlah_unit)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        // 5. Keterangan Dokter
        OutlinedTextField(
            value = resepDetails.keteranganDokter,
            onValueChange = { onValueChange(resepDetails.copy(keteranganDokter = it)) },
            label = { Text(stringResource(R.string.keterangan_dokter)) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            shape = RoundedCornerShape(12.dp)
        )
    }

    // Logic Dialog Kalender
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = dateFormatter.format(Date(millis))
                        onValueChange(resepDetails.copy(tanggalInput = selectedDate))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}