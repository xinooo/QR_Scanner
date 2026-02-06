package com.xinooo.qrcode.ui.views

import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
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
            qrAnalyzer = QrAnalyzer() { barcode ->
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
