package com.example.medstock.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
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
import com.example.medstock.view.route.DestinasiDetailResep
import com.example.medstock.viewmodel.DetailResepViewModel
import com.example.medstock.viewmodel.ResepDetails
import com.example.medstock.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDetailResep(
    navigateToEditResep: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailResepViewModel = viewModel(factory = PenyediaViewModel.FactoryDetailResep(0))
) {
    val coroutineScope = rememberCoroutineScope()
    val resepDetailUiState by viewModel.uiState.collectAsState()
    var deleteConfirmationRequired by remember { mutableStateOf(false) }

    // Gunakan Box sebagai container utama agar bisa menumpuk Header Biru dan Konten Putih
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Background Abu
    ) {
        // 1. HEADER BACKGROUND BIRU
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color(0xFF4C4CFF))
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // 2. CUSTOM TOP BAR (Back Button + Judul)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = navigateBack,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(DestinasiDetailResep.titleRes),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. BODY KONTEN (Panggil fungsi Body yang sudah dipercantik)
            DetailResepBody(
                resepDetails = resepDetailUiState.resepDetails,
                onEditClick = { navigateToEditResep(resepDetailUiState.resepDetails.id) },
                onDeleteClick = { deleteConfirmationRequired = true },
                modifier = Modifier.weight(1f)
            )
        }
    }

    // Dialog Konfirmasi Hapus (Logika Tetap Sama)
    if (deleteConfirmationRequired) {
        DeleteConfirmationDialog(
            message = stringResource(R.string.tanya_hapus_resep, resepDetailUiState.resepDetails.namaPasien),
            onDeleteConfirm = {
                coroutineScope.launch {
                    viewModel.deleteResep()
                    navigateBack()
                }
                deleteConfirmationRequired = false
            },
            onDeleteCancel = { deleteConfirmationRequired = false }
        )
    }
}

// --- Komponen Body yang Direvisi (Tampilan Card Putih) ---

@Composable
fun DetailResepBody(
    resepDetails: ResepDetails,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxSize(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // LIST DATA DETAIL
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

                // Section Info Pasien
                Text("Informasi Pasien", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF4C4CFF))

                DetailItemRow(
                    icon = Icons.Default.Person,
                    label = stringResource(R.string.nama_pasien),
                    value = resepDetails.namaPasien
                )
                DetailItemRow(
                    icon = Icons.Default.CalendarToday,
                    label = stringResource(R.string.tanggal),
                    value = resepDetails.tanggalDisplay
                )

                Divider(color = Color.LightGray.copy(alpha = 0.5f))

                // Section Info Obat
                Text("Detail Obat", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF4C4CFF))

                DetailItemRow(
                    icon = Icons.Default.LocalPharmacy,
                    label = stringResource(R.string.obat_diberikan),
                    value = resepDetails.namaObat
                )
                DetailItemRow(
                    icon = Icons.Default.Numbers,
                    label = stringResource(R.string.jumlah_unit),
                    value = "${resepDetails.jumlah} unit"
                )

                // Keterangan Dokter
                DetailItemRow(
                    icon = Icons.Default.Description,
                    label = stringResource(R.string.keterangan_dokter),
                    value = resepDetails.keteranganDokter
                )
            }

            Spacer(Modifier.height(32.dp))

            // TOMBOL AKSI (Edit & Delete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tombol Delete (Merah)
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

                // Tombol Edit (Biru Utama)
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
}

// --- Helper UI Baru (Row dengan Ikon Biru) ---
@Composable
fun DetailItemRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ikon dalam lingkaran biru muda
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

// Dialog Konfirmasi tetap sama
@Composable
fun DeleteConfirmationDialog(
    message: String,
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDeleteCancel,
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(message) },
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