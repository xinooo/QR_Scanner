package com.xinooo.qrcode.core.scanner

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.atomic.AtomicBoolean

class QrAnalyzer(
    private val barcodeValidator: ((Barcode, ImageProxy) -> Boolean),
    private val onQrDetected: (QrScanResult, Bitmap?) -> Unit,
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
        // 固定傳入 0 度旋轉給 ML Kit，讓其回傳原始 Buffer 座標系下的 boundingBox。
        // 這樣在 UI 層使用 ImageProxyTransformFactory 轉換時才能正確對齊。
        val image = InputImage.fromMediaImage(mediaImage, 0)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isEmpty()) return@addOnSuccessListener

                val barcode = barcodes.first()

                // 檢查是否在指定範圍內
                val isValid = barcodeValidator.invoke(barcode, imageProxy)
                if (!isValid) return@addOnSuccessListener

                if (scanningLock.compareAndSet(false, true)) {
                    val bitmap = imageProxy.toBitmap()
                    val rotatedBitmap = rotateBitmap(bitmap, imageProxy.imageInfo.rotationDegrees)
                    onQrDetected(QrScanResult.Success(barcode), rotatedBitmap)
                }

            }
            .addOnFailureListener {
                onQrDetected(QrScanResult.Error(it), null)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
        if (rotationDegrees == 0) return bitmap
        val matrix = Matrix()
        matrix.postRotate(rotationDegrees.toFloat())
        return Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )
    }
}
