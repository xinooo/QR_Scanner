package com.xinooo.qrcode.ui.views

import android.graphics.RectF
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.TransformExperimental
import androidx.camera.view.transform.CoordinateTransform
import androidx.camera.view.transform.ImageProxyTransformFactory
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.common.Barcode
import com.xinooo.qrcode.R
import com.xinooo.qrcode.databinding.ActivityQrcodeScannerBinding
import com.xinooo.qrcode.ui.BaseActivity
import com.xinooo.qrcode.utils.Logger
import com.xinooo.qrcode.utils.QrAnalyzer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QRCodeScannerActivity : BaseActivity<ActivityQrcodeScannerBinding>() {

    private lateinit var cameraExecutor: ExecutorService
    private var qrAnalyzer: QrAnalyzer? = null

    override fun getLayoutId(): Int = R.layout.activity_qrcode_scanner

    override fun initLayoutView() {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun initViewData() {
        binding.previewView.post {
            startCamera()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.previewView.surfaceProvider
            }

            // 建立 QrAnalyzer
            qrAnalyzer = QrAnalyzer(
                barcodeValidator = { barcode, imageProxy ->
                    isWithinScannerFrame(barcode, imageProxy)
                }
            ) { barcode ->
                val rawValue = barcode.rawValue
                if (rawValue != null) {
                    onQRCodeScanned(rawValue)
                }
            }

            // 建立 ImageAnalysis
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, qrAnalyzer!!)
                }

            // 使用 UseCaseGroup 並套用 ViewPort 確保預覽與分析座標一致
            val viewPort = binding.previewView.viewPort
            val useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageAnalyzer)
                .apply {
                    viewPort?.let { setViewPort(it) }
                }
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, useCaseGroup)
            } catch (exc: Exception) {
                Logger.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    @OptIn(TransformExperimental::class)
    private fun isWithinScannerFrame(barcode: Barcode, imageProxy: ImageProxy): Boolean {
        val boundingBox = barcode.boundingBox ?: return false

        return try {
            val factory = ImageProxyTransformFactory()
            // source transform 是基於 imageProxy.cropRect 的
            val source = factory.getOutputTransform(imageProxy)
            val target = binding.previewView.outputTransform ?: return false
            val coordinateTransform = CoordinateTransform(source, target)

            val barcodeRectF = RectF(boundingBox)

            // 手動扣除 CropRect 的偏移量，座標才能對齊
            val cropRect = imageProxy.cropRect
            barcodeRectF.offset(-cropRect.left.toFloat(), -cropRect.top.toFloat())

            // 轉換到 PreviewView 座標系
            coordinateTransform.mapRect(barcodeRectF)

            // 取得掃描框在 PreviewView 中的相對位置
            val frameLoc = IntArray(2)
            binding.scannerFrame.getLocationInWindow(frameLoc)
            val previewLoc = IntArray(2)
            binding.previewView.getLocationInWindow(previewLoc)

            val relativeLeft = (frameLoc[0] - previewLoc[0]).toFloat()
            val relativeTop = (frameLoc[1] - previewLoc[1]).toFloat()

            val frameRectF = RectF(
                relativeLeft,
                relativeTop,
                relativeLeft + binding.scannerFrame.width,
                relativeTop + binding.scannerFrame.height
            )

            // 判斷條碼中心點是否在方框內
            frameRectF.contains(barcodeRectF.centerX(), barcodeRectF.centerY())

        } catch (e: Exception) {
            Logger.e(TAG, "Coordinate transform failed", e)
            false
        }
    }

    private fun onQRCodeScanned(result: String) {
        runOnUiThread {
            Logger.i(TAG, "QR Code Result: $result")
            Toast.makeText(this, "Scanned:\n$result", Toast.LENGTH_SHORT).show()
        }
    }

    fun resumeScanning() {
        qrAnalyzer?.resumeScanning()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
