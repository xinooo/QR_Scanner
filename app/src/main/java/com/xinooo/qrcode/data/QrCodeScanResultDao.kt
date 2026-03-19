package com.xinooo.qrcode.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QrCodeScanResultDao {
    @Insert
    suspend fun insert(result: QrCodeScanResult)

    @Query("SELECT * FROM qr_code_scan_results ORDER BY timestamp DESC")
    fun getAllScanResults(): Flow<List<QrCodeScanResult>>
}