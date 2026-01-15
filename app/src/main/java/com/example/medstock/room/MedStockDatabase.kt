package com.example.medstock.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.medstock.room.dao.ObatDao
import com.example.medstock.room.dao.ResepDao
import com.example.medstock.room.entity.Obat
import com.example.medstock.room.entity.Resep

@Database(
    entities = [Obat::class, Resep::class],
    version = 1,
    exportSchema = false
)
abstract class MedStockDatabase : RoomDatabase() {

    abstract fun obatDao(): ObatDao
    abstract fun resepDao(): ResepDao

    companion object {
        @Volatile
        private var Instance: MedStockDatabase? = null

        fun getDatabase(context: Context): MedStockDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, MedStockDatabase::class.java, "medstock_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}