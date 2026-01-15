package com.example.medstock.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medstock.R
import com.example.medstock.viewmodel.HomeViewModel
import com.example.medstock.viewmodel.provider.PenyediaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanAdminHome(
    navigateToEntryResep: () -> Unit,
    navigateToEntryObat: () -> Unit,
    navigateToDetailResep: (Int) -> Unit,
    navigateToEditObat: (Int) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.FactoryHome)
) {
    val homeUiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var tabIndex by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    val tabs = listOf("Pencatatan Resep", "Manajemen Stok")

    // --- STATE DATE PICKER ---
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    val datePickerState = rememberDatePickerState()
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // --- LOGIKA FILTER (Search + Tanggal) ---
    // Hanya berlaku untuk Resep. Untuk Stok obat biasanya hanya search nama.
    val filteredResep = homeUiState.daftarResep.filter { resep ->
        val matchQuery = resep.namaPasien.contains(searchQuery, ignoreCase = true) ||
                resep.namaObat.contains(searchQuery, ignoreCase = true)

        val matchDate = if (selectedDateMillis != null) {
            val resepDate = Date(resep.tanggal)
            val filterDate = Date(selectedDateMillis!!)
            val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            sdf.format(resepDate) == sdf.format(filterDate)
        } else {
            true
        }
        matchQuery && matchDate
    }

    // Filter untuk Stok Obat (Hanya Search)
    val filteredObat = homeUiState.daftarObat.filter { obat ->
        obat.namaObat.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            // CUSTOM HEADER ADMIN
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_medstock),
                        contentDescription = "Logo",
                        modifier = Modifier.size(40.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "MedStock",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4285F4)
                        )
                        Text(
                            text = "Sistem Manajemen Apotek",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }

                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("LOGOUT", fontSize = 12.sp)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // TAB NAVIGASI
            TabRow(
                selectedTabIndex = tabIndex,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF4285F4),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                        color = Color(0xFF4285F4)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (tabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selectedContentColor = Color(0xFF4285F4),
                        unselectedContentColor = Color.Gray
                    )
                }
            }

            // KONTEN UTAMA
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
            ) {
                // Background Gradient Biru
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp) // Tinggi disesuaikan agar muat filter
                        .background(Color(0xFF4C4CFF))
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    // 1. SEARCH BAR
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { Text("Cari data...", fontSize = 14.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent
                        ),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. FILTER TANGGAL & PDF (Hanya Muncul di Tab Resep)
                    if (tabIndex == 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Date Picker Button
                            OutlinedButton(
                                onClick = { showDatePicker = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .background(Color.White, RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                border = null,
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.DateRange, contentDescription = null, tint = Color.Gray)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (selectedDateMillis != null) dateFormatter.format(Date(selectedDateMillis!!)) else "hh/bb/tttt",
                                            color = if (selectedDateMillis != null) Color.Black else Color.Gray
                                        )
                                    }
                                    if (selectedDateMillis != null) {
                                        Icon(
                                            Icons.Filled.Close,
                                            contentDescription = "Reset",
                                            tint = Color.Gray,
                                            modifier = Modifier.clickable { selectedDateMillis = null }
                                        )
                                    }
                                }
                            }

                            // PDF Export Button
                            Button(
                                onClick = {
                                    if (filteredResep.isNotEmpty()) {
                                        PdfUtils.generateResepPdf(context, filteredResep)
                                    } else {
                                        Toast.makeText(context, "Tidak ada data untuk dicetak", Toast.LENGTH_SHORT).show()
                                    }                                },
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                            ) {
                                Icon(Icons.Filled.PictureAsPdf, contentDescription = "PDF", tint = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. CARD KONTEN PUTIH
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize() // Pastikan Column mengisi Card
                        ) {

                            // Header Judul & Tombol Tambah
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (tabIndex == 0) "Riwayat Resep" else "Daftar Obat",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                // Tombol Tambah (Muncul di KEDUA tab untuk Admin)
                                Button(
                                    onClick = if (tabIndex == 0) navigateToEntryResep else navigateToEntryObat,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (tabIndex == 0) "Tambah Resep" else "Tambah Obat", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // DAFTAR KONTEN
                            if (tabIndex == 0) {
                                // --- TAB 0: RESEP (Gunakan filteredResep) ---
                                if (filteredResep.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (selectedDateMillis != null) "Tidak ada resep di tanggal ini" else "Belum ada data resep",
                                            color = Color.Gray
                                        )
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.weight(1f), // Penting untuk scroll
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(filteredResep) { resep ->
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { navigateToDetailResep(resep.id) },
                                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                            ) {
                                                Column(modifier = Modifier.padding(16.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(Icons.Filled.Person, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(16.dp))
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text(text = resep.namaPasien, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                                        }
                                                        Text(text = dateFormatter.format(Date(resep.tanggal)), fontSize = 12.sp, color = Color.Gray)
                                                    }

                                                    Spacer(modifier = Modifier.height(4.dp))

                                                    Text(
                                                        text = "${resep.namaObat} (${resep.jumlah} unit)",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = Color.DarkGray
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                // --- TAB 1: STOK OBAT (Gunakan filteredObat) ---
                                if (filteredObat.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("Belum ada data obat", color = Color.Gray)
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.weight(1f), // Penting untuk scroll
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(filteredObat) { obat ->
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { navigateToEditObat(obat.id) },
                                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .padding(16.dp)
                                                        .fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Column {
                                                        Text(text = obat.namaObat, fontWeight = FontWeight.Bold)
                                                        Text(text = "Stok: ${obat.stok} unit", fontSize = 12.sp, color = Color.Gray)
                                                    }
                                                    Column(horizontalAlignment = Alignment.End) {
                                                        Text(text = "Rp${obat.harga}", fontWeight = FontWeight.SemiBold, color = Color(0xFF4CAF50))
                                                        Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // POPUP DATE PICKER DIALOG
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        selectedDateMillis = datePickerState.selectedDateMillis
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}