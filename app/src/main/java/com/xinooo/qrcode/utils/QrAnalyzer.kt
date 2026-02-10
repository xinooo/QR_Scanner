package com.xinooo.qrcode.utils

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.atomic.AtomicBoolean

class QrAnalyzer(
    private val barcodeValidator: ((Barcode, ImageProxy) -> Boolean),
    private val onQrDetected: (Barcode) -> Unit,
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()

    private val scanningLock = AtomicBoolean(false)

    // 啟動掃描
    fun enableScanning() {
        scanningLock.set(false)
    }

    // 停止掃描
    fun disableScanning() {
        scanningLock.set(true)
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        if (scanningLock.get()) {
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
                if (barcodes.isEmpty()) return@addOnSuccessListener

                val barcode = barcodes.first()

                // 檢查是否在指定範圍內
                val isValid = barcodeValidator.invoke(barcode, imageProxy)
                if (!isValid) return@addOnSuccessListener

                if (scanningLock.compareAndSet(false, true)) {
                    onQrDetected(barcode)
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
