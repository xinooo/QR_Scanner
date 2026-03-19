package com.xinooo.qrcode.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [QrCodeScanResult::class], version = 1, exportSchema = false)
abstract class QrCodeDatabase : RoomDatabase() {

    abstract fun qrCodeScanResultDao(): QrCodeScanResultDao

    companion object {
        @Volatile
        private var INSTANCE: QrCodeDatabase? = null

        fun getDatabase(context: Context): QrCodeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QrCodeDatabase::class.java,
                    "qr_code_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}