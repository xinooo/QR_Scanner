package com.xinooo.qrcode.core.scanner

import android.graphics.Bitmap
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QrImageScanner {

    private val scanner by lazy {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )
    }

    fun scan(bitmap: Bitmap, callback: (QrScanResult) -> Unit) {
        val image = InputImage.fromBitmap(bitmap, 0)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val barcode = barcodes.firstOrNull()
                if (barcode != null) {
                    callback(QrScanResult.Success(barcode))
                } else {
                    callback(QrScanResult.NotFound)
                }
            }
            .addOnFailureListener {
                callback(QrScanResult.Error(it))
            }
    }

}