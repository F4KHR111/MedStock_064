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
import com.example.medstock.viewmodel.EntryResepViewModel
import com.example.medstock.viewmodel.ResepDetails
import com.example.medstock.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEntryResep(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EntryResepViewModel = viewModel(factory = PenyediaViewModel.FactoryEntryResep)
) {
    val coroutineScope = rememberCoroutineScope()
    val resepUiState = viewModel.resepUiState

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
                    text = "Tambah Resep Baru",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Card Form
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                EntryResepBody(
                    resepUiState = resepUiState.resepDetails,
                    daftarObat = resepUiState.daftarObatDropdown,
                    onResepValueChange = viewModel::updateUiState,
                    onSaveClick = {
                        coroutineScope.launch {
                            viewModel.saveResep()
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
fun EntryResepBody(
    resepUiState: ResepDetails,
    daftarObat: List<com.example.medstock.room.entity.Obat>,
    onResepValueChange: (ResepDetails) -> Unit,
    onSaveClick: () -> Unit,
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
            text = "Lengkapi Data Resep",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF4C4CFF),
            fontWeight = FontWeight.Bold
        )

        ResepInputForm(
            resepDetails = resepUiState,
            daftarObat = daftarObat,
            onValueChange = onResepValueChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSaveClick,
            enabled = isEntryValid,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C4CFF)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Icon(Icons.Filled.Save, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.btn_simpan), fontWeight = FontWeight.Bold)
        }

        if (!isEntryValid) {
            Text(
                text = stringResource(R.string.required_field),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResepInputForm(
    resepDetails: ResepDetails,
    daftarObat: List<com.example.medstock.room.entity.Obat>,
    modifier: Modifier = Modifier,
    onValueChange: (ResepDetails) -> Unit = {}
) {
    var expandedDropdown by remember { mutableStateOf(false) }

    // --- STATE DATE PICKER ---
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Format Database

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

        // 2. Tanggal (DENGAN DATE PICKER)
        OutlinedTextField(
            value = resepDetails.tanggalInput,
            onValueChange = { }, // Read Only, tidak bisa diketik
            label = { Text(stringResource(R.string.tanggal)) },
            placeholder = { Text("YYYY-MM-DD") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }, // Klik text field juga buka kalender
            enabled = true, // Tetap enabled agar bisa diklik
            readOnly = true, // Tidak muncul keyboard
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

        // 3. Obat Dropdown
        Box {
            OutlinedTextField(
                value = daftarObat.find { it.id == resepDetails.obatId.toIntOrNull() }?.namaObat ?: "",
                onValueChange = {},
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
                            onValueChange(resepDetails.copy(obatId = obat.id.toString()))
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

        // 5. Keterangan
        OutlinedTextField(
            value = resepDetails.keteranganDokter,
            onValueChange = { onValueChange(resepDetails.copy(keteranganDokter = it)) },
            label = { Text(stringResource(R.string.keterangan_dokter)) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            shape = RoundedCornerShape(12.dp)
        )
    }

    // --- LOGIKA DATE PICKER DIALOG ---
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