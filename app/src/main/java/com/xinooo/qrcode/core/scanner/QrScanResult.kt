package com.xinooo.qrcode.core.scanner

import com.google.mlkit.vision.barcode.common.Barcode

sealed class QrScanResult {
    data class Success(val barcode: Barcode) : QrScanResult()
    object NotFound : QrScanResult()
    data class Error(val exception: Exception) : QrScanResult()
}