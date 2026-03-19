package com.xinooo.qrcode.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class QrCodeScanResultRepository(context: Context) {
    private val qrCodeScanResultDao = QrCodeDatabase.getDatabase(context).qrCodeScanResultDao()

    suspend fun insertScanResult(result: String) {
        val scanResult = QrCodeScanResult(result = result, timestamp = System.currentTimeMillis())
        qrCodeScanResultDao.insert(scanResult)
    }

    fun getAllScanResults(): Flow<List<QrCodeScanResult>> {
        return qrCodeScanResultDao.getAllScanResults()
    }
}