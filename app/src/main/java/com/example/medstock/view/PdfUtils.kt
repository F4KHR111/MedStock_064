package com.example.medstock.view

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.medstock.room.entity.Resep
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfUtils {

    fun generateResepPdf(context: Context, daftarResep: List<Resep>) {
        val pdfDocument = PdfDocument()
        val paint = Paint()

        // 1. Konfigurasi Halaman A4
        // Lebar: 595, Tinggi: 842 (Standar A4 72dpi)
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // 2. Menggambar JUDUL
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 24f
        paint.color = android.graphics.Color.BLACK
        canvas.drawText("Laporan Resep MedStock", 50f, 60f, paint)

        // Sub-judul (Tanggal Cetak)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 12f
        val dateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
        val tanggalCetak = dateFormat.format(Date())
        canvas.drawText("Dicetak pada: $tanggalCetak", 50f, 85f, paint)

        // 3. Menggambar HEADER TABEL
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        // Garis Header
        canvas.drawLine(50f, 110f, 545f, 110f, paint)
        canvas.drawLine(50f, 140f, 545f, 140f, paint)

        paint.style = Paint.Style.FILL
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12f

        // Kolom Header
        canvas.drawText("Pasien", 60f, 130f, paint)
        canvas.drawText("Obat", 200f, 130f, paint)
        canvas.drawText("Jumlah", 350f, 130f, paint)
        canvas.drawText("Tanggal", 450f, 130f, paint)

        // 4. Menggambar ISI DATA
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        var yPos = 160f

        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        for (resep in daftarResep) {
            // Cek jika halaman penuh (sederhana)
            if (yPos > 800f) {
                // Di tutorial ini kita batasi 1 halaman dulu agar simpel
                break
            }

            canvas.drawText(resep.namaPasien, 60f, yPos, paint)
            canvas.drawText(resep.namaObat, 200f, yPos, paint)
            canvas.drawText("${resep.jumlah} Unit", 350f, yPos, paint)

            val tgl = try {
                dateFormatter.format(Date(resep.tanggal))
            } catch (e: Exception) { "-" }
            canvas.drawText(tgl, 450f, yPos, paint)

            yPos += 30f // Jarak antar baris
        }

        pdfDocument.finishPage(page)

        // 5. Menyimpan File (Support Android 10+ tanpa Permission ribet)
        val fileName = "Laporan_Resep_${System.currentTimeMillis()}.pdf"

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ (Pakai MediaStore)
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                    }
                    Toast.makeText(context, "PDF Berhasil disimpan di Downloads!", Toast.LENGTH_LONG).show()
                }
            } else {
                // Android 9 ke bawah (Pakai cara lama - ExternalStorage)
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                pdfDocument.writeTo(FileOutputStream(file))
                Toast.makeText(context, "PDF Disimpan: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal mencetak PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }
}