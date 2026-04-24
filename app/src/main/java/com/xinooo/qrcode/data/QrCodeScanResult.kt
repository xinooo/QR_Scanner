package com.xinooo.qrcode.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "qr_code_scan_results")
data class QrCodeScanResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val result: String,
    val timestamp: Long
) : Serializable