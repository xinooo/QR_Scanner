package com.xinooo.qrcode.ui

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
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.barcode.common.Barcode
import com.webrtc.cc.ui.BaseFragment
import com.xinooo.qrcode.R
import com.xinooo.qrcode.databinding.FragmentQrcodeScannerBinding
import com.xinooo.qrcode.utils.Logger
import com.xinooo.qrcode.utils.QrAnalyzer
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QRCodeScannerFragment: BaseFragment<FragmentQrcodeScannerBinding>() {

    private val cameraExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }
    private val preview by lazy { Preview.Builder().build() }
    private val qrAnalyzer by lazy {
        QrAnalyzer(
            barcodeValidator = { barcode, imageProxy ->
                isWithinScannerFrame(barcode, imageProxy)
            }
        ) { barcode ->
            val rawValue = barcode.rawValue
            if (rawValue != null) {
                onQRCodeScanned(rawValue)
            }
        }
    }
    private val imageAnalyzer by lazy {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_qrcode_scanner
    }

    override fun initLayoutView() {
        binding.titleBar.setAppTitle(getString(R.string.nav_scanner))
        binding.titleBar.setLeftBtnVisibility(false)
    }

    override fun initViewData() {
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            preview.surfaceProvider = binding.previewView.surfaceProvider

            imageAnalyzer.setAnalyzer(cameraExecutor, qrAnalyzer)

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

        }, ContextCompat.getMainExecutor(requireContext()))
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
        viewLifecycleOwner.lifecycleScope.launch {
            Logger.i(TAG, "QR Code Result: $result")
            Toast.makeText(requireContext(), "Scanned:\n$result", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        qrAnalyzer.disableScanning()
    }

    override fun onResume() {
        super.onResume()
        qrAnalyzer.enableScanning()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }

}