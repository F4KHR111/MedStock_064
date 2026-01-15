package com.example.medstock.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medstock.R
import com.example.medstock.viewmodel.EditObatViewModel
import com.example.medstock.viewmodel.ObatDetails
import com.example.medstock.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch

// Pastikan ObatInputForm terimport dari HalamanEntryObat
// Jika masih merah, arahkan kursor ke ObatInputForm lalu tekan Alt+Enter -> Import

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEditObat(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditObatViewModel = viewModel(factory = PenyediaViewModel.FactoryEditObat(0))
) {
    var isEditMode by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val obatUiState by viewModel.uiState.collectAsState()

    // Dialog State
    var deleteConfirmationRequired by remember { mutableStateOf(false) }

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
                    onClick = {
                        if (isEditMode) isEditMode = false else onNavigateUp()
                    },
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = if (isEditMode) stringResource(R.string.edit_obat_title) else stringResource(R.string.detail_obat_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Card Putih Utama
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                if (isEditMode) {
                    // MODE EDIT
                    EditObatFormBody(
                        obatUiState = viewModel.obatUiState.obatDetails,
                        onObatValueChange = viewModel::updateUiState,
                        onUpdateClick = {
                            coroutineScope.launch {
                                viewModel.updateObat()
                                isEditMode = false
                            }
                        },
                        isEntryValid = viewModel.obatUiState.isEntryValid,
                        modifier = Modifier.padding(24.dp)
                    )
                } else {
                    // MODE DETAIL
                    DetailObatBody(
                        obatDetails = obatUiState.obatDetails,
                        onEditClick = {
                            viewModel.updateUiState(obatUiState.obatDetails)
                            isEditMode = true
                        },
                        onDeleteClick = { deleteConfirmationRequired = true },
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        }
    }

    // Panggil Dialog yang sudah direname (Private) agar tidak bentrok
    if (deleteConfirmationRequired) {
        DeleteObatDialog(
            onDeleteConfirm = {
                coroutineScope.launch {
                    viewModel.deleteObat()
                    navigateBack()
                }
                deleteConfirmationRequired = false
            },
            onDeleteCancel = { deleteConfirmationRequired = false }
        )
    }
}

// --- Komponen Detail Body ---
@Composable
fun DetailObatBody(
    obatDetails: ObatDetails,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text("Informasi Produk", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF4C4CFF))

            DetailItemRowObat(
                icon = Icons.Default.MedicalServices,
                label = stringResource(R.string.nama_obat),
                value = obatDetails.namaObat
            )

            Divider(color = Color.LightGray.copy(alpha = 0.5f))

            DetailItemRowObat(
                icon = Icons.Default.Inventory,
                label = stringResource(R.string.stok),
                value = "${obatDetails.stok} unit"
            )

            DetailItemRowObat(
                icon = Icons.Default.AttachMoney,
                label = stringResource(R.string.harga_satuan),
                value = "Rp ${obatDetails.harga}"
            )

            DetailItemRowObat(
                icon = Icons.Default.CalendarToday,
                label = stringResource(R.string.tanggal_kadaluarsa),
                value = obatDetails.tanggalKadaluarsaInput
            )
        }

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onDeleteClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(50.dp)
            ) {
                Icon(Icons.Filled.Delete, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.delete), fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onEditClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C4CFF)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(50.dp)
            ) {
                Icon(Icons.Filled.Edit, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("EDIT", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- Komponen Edit Form Body ---
@Composable
fun EditObatFormBody(
    obatUiState: ObatDetails,
    onObatValueChange: (ObatDetails) -> Unit,
    onUpdateClick: () -> Unit,
    isEntryValid: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Edit Informasi", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF4C4CFF))

        // Panggil Form Input (Pastikan diimport dari HalamanEntryObat)
        ObatInputForm(
            obatDetails = obatUiState,
            onValueChange = onObatValueChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onUpdateClick,
            enabled = isEntryValid,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C4CFF)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Icon(Icons.Filled.Save, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.btn_update), fontWeight = FontWeight.Bold)
        }
    }
}

// --- Helper UI: Row dengan Ikon Biru ---
@Composable
fun DetailItemRowObat(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF4285F4).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        }
    }
}

// --- Dialog Konfirmasi Hapus (RENAME JADI PRIVATE AGAR TIDAK BENTROK) ---
@Composable
private fun DeleteObatDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDeleteCancel,
        title = { Text(stringResource(R.string.attention)) },
        text = { Text("Apakah Anda yakin ingin menghapus data obat ini secara permanen?") },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(stringResource(R.string.yes), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(stringResource(R.string.no))
            }
        }
    )
}