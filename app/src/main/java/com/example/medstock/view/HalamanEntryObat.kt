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
import com.example.medstock.viewmodel.EntryObatViewModel
import com.example.medstock.viewmodel.ObatDetails
import com.example.medstock.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEntryObat(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EntryObatViewModel = viewModel(factory = PenyediaViewModel.FactoryEntryObat)
) {
    val coroutineScope = rememberCoroutineScope()
    val obatUiState = viewModel.obatUiState

    Box(
        modifier = modifier.fillMaxSize().background(Color(0xFFF5F5F5))
    ) {
        // Header Biru
        Box(modifier = Modifier.fillMaxWidth().height(220.dp).background(Color(0xFF4C4CFF)))

        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateUp,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape).size(40.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Tambah Obat Baru",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Card Form
            Card(
                modifier = Modifier.fillMaxSize().weight(1f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                EntryObatBody(
                    obatUiState = obatUiState.obatDetails,
                    onObatValueChange = viewModel::updateUiState,
                    onSaveClick = {
                        coroutineScope.launch {
                            viewModel.saveObat()
                            navigateBack()
                        }
                    },
                    isEntryValid = obatUiState.isEntryValid,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    }
}

@Composable
fun EntryObatBody(
    obatUiState: ObatDetails,
    onObatValueChange: (ObatDetails) -> Unit,
    onSaveClick: () -> Unit,
    isEntryValid: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Lengkapi Data Stok", style = MaterialTheme.typography.titleMedium, color = Color(0xFF4C4CFF), fontWeight = FontWeight.Bold)

        ObatInputForm(
            obatDetails = obatUiState,
            onValueChange = onObatValueChange
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
            Text(stringResource(R.string.required_field), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObatInputForm(
    obatDetails: ObatDetails,
    modifier: Modifier = Modifier,
    onValueChange: (ObatDetails) -> Unit = {}
) {
    // State Date Picker untuk Obat
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = obatDetails.namaObat,
            onValueChange = { onValueChange(obatDetails.copy(namaObat = it)) },
            label = { Text(stringResource(R.string.nama_obat)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = obatDetails.stok,
            onValueChange = { if (it.all { char -> char.isDigit() }) onValueChange(obatDetails.copy(stok = it)) },
            label = { Text(stringResource(R.string.stok)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = obatDetails.harga,
            onValueChange = { if (it.all { char -> char.isDigit() }) onValueChange(obatDetails.copy(harga = it)) },
            label = { Text(stringResource(R.string.harga_satuan)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        // DATE PICKER UNTUK KADALUARSA
        OutlinedTextField(
            value = obatDetails.tanggalKadaluarsaInput,
            onValueChange = { },
            label = { Text(stringResource(R.string.tanggal_kadaluarsa)) },
            placeholder = { Text("YYYY-MM-DD") },
            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
            readOnly = true,
            enabled = true,
            trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Filled.DateRange, contentDescription = null) } },
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
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = dateFormatter.format(Date(millis))
                        onValueChange(obatDetails.copy(tanggalKadaluarsaInput = selectedDate))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Batal") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}