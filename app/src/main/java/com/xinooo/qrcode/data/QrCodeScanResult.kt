package com.xinooo.qrcode.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_code_scan_results")
data class QrCodeScanResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val result: String,
    val timestamp: Long
)