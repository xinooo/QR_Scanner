package com.xinooo.qrcode.utils

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QrAnalyzer(
    private val barcodeValidator: ((Barcode, ImageProxy) -> Boolean),
    private val onQrDetected: (Barcode) -> Unit,
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()

    @Volatile
    private var isScanningEnabled = true

    fun resumeScanning() {
        isScanningEnabled = true
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        if (!isScanningEnabled) {
            imageProxy.close()
            return
        }
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (!isScanningEnabled) return@addOnSuccessListener

                if (isScanningEnabled && barcodes.isNotEmpty()) {
                    val barcode = barcodes.first()
                    
                    // 檢查是否在指定範圍內
                    val isValid = barcodeValidator.invoke(barcode, imageProxy)
                    
                    if (isValid) {
                        isScanningEnabled = false // 掃描成功後停止
                        onQrDetected(barcode)
                    }
                }
            }
            .addOnFailureListener {
                Logger.e("QrAnalyzer", "Barcode scanning failed", it)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}
