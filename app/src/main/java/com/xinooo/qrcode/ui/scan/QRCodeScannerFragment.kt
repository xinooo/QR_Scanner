package com.xinooo.qrcode.ui.scan

import android.graphics.RectF
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.TransformExperimental
import androidx.camera.view.transform.CoordinateTransform
import androidx.camera.view.transform.ImageProxyTransformFactory
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.barcode.common.Barcode
import com.xinooo.qrcode.core.base.BaseFragment
import com.xinooo.qrcode.R
import com.xinooo.qrcode.databinding.FragmentQrcodeScannerBinding
import com.xinooo.qrcode.utils.Logger
import com.xinooo.qrcode.core.scanner.QrAnalyzer
import com.xinooo.qrcode.core.scanner.QrImageScanner
import com.xinooo.qrcode.core.scanner.QrScanResult
import com.xinooo.qrcode.utils.BitmapUtils
import com.xinooo.qrcode.data.QrCodeScanResultRepository
import com.xinooo.qrcode.data.SettingsManager
import com.xinooo.qrcode.utils.AdManager
import com.xinooo.qrcode.utils.ScanActionManager
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
        ) { result, bitmap ->
            handleResult(result)
        }
    }
    private val imageAnalyzer by lazy {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    private val qrCodeScanResultRepository by lazy {
        QrCodeScanResultRepository(requireContext())
    }

    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    // 紀錄切換頁面前閃光燈是否為開啟狀態
    private var wasFlashlightOn = false

    override fun getLayoutId(): Int {
        return R.layout.fragment_qrcode_scanner
    }

    override fun initLayoutView() {
        binding.titleBar.setAppTitle(getString(R.string.nav_scanner))
        binding.titleBar.setLeftBtnVisibility(false)
        binding.btnImageScan.setOnClickListener { pickImage.launch("image/*") }
        
        binding.btnFlashlight.setOnClickListener {
            toggleFlashlight()
        }

        // Initialize and load AdView
        AdManager.loadBannerAd(binding.adView)
    }

    override fun initViewData() {
        // 預先載入設定
        SettingsManager.loadSettings(requireContext())
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val provider: ProcessCameraProvider = cameraProviderFuture.get()
            this.cameraProvider = provider

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
                provider.unbindAll()
                // 使用 viewLifecycleOwner 以確保在 ViewPager 切換時正確釋放資源
                camera = provider.bindToLifecycle(viewLifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, useCaseGroup)
                observeFlashlightState()

                // 如果綁定完成時正處於 Resume 且之前是開啟狀態，則恢復閃光燈
                if (isResumed && wasFlashlightOn) {
                    camera?.cameraControl?.enableTorch(true)
                }
            } catch (exc: Exception) {
                Logger.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    /**
     * 監聽閃光燈狀態並更新 UI
     */
    private fun observeFlashlightState() {
        camera?.cameraInfo?.torchState?.observe(viewLifecycleOwner) { state ->
            binding.btnFlashlight.setImageResource(R.drawable.ic_flash)
            val isOn = state == TorchState.ON
            val colorRes = if (isOn) R.color.flash_on else R.color.white
            binding.btnFlashlight.imageTintList = ContextCompat.getColorStateList(requireContext(), colorRes)
        }
    }

    private fun toggleFlashlight() {
        val isTorchOn = camera?.cameraInfo?.torchState?.value == TorchState.ON
        wasFlashlightOn = !isTorchOn
        camera?.cameraControl?.enableTorch(wasFlashlightOn)
    }

    @OptIn(TransformExperimental::class)
    private fun isWithinScannerFrame(barcode: Barcode, imageProxy: ImageProxy): Boolean {
        val boundingBox = barcode.boundingBox ?: return false

        return try {
            val factory = ImageProxyTransformFactory()
            // factory.getOutputTransform(imageProxy) 會自動處理 cropRect 引起的偏移
            val source = factory.getOutputTransform(imageProxy)
            val target = binding.previewView.outputTransform ?: return false
            val coordinateTransform = CoordinateTransform(source, target)

            val barcodeRectF = RectF(boundingBox)

            // ML Kit 座標是相對於整個 Buffer 的，需扣除 cropRect 的偏移才能與 TransformFactory 對齊
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

            // 嚴格判定：條碼矩形必須完全包含在掃描框內
            frameRectF.contains(barcodeRectF)

        } catch (e: Exception) {
            Logger.e(TAG, "Coordinate transform failed", e)
            false
        }
    }

    private fun onQRCodeScanned(result: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            Logger.i(TAG, "QR Code Result: $result")

            // 1. 保存到資料庫
            qrCodeScanResultRepository.insertScanResult(result)

            // 2. 先顯示互動對話框 (不阻塞執行緒)
            ScanResultDialog.newInstance(result) {
                qrAnalyzer.enableScanning()
            }.show(parentFragmentManager, "ScanResultDialog")

            // 3. 呼叫獨立的 Action Manager 執行自動化動作 (聲音、複製、自動開啟)
            ScanActionManager.executeActions(requireContext(), result)
        }
    }

    private fun handleResult(result: QrScanResult) {
        when (result) {
            is QrScanResult.Success -> {
                result.barcode.rawValue?.let { onQRCodeScanned(it) }
            }
            QrScanResult.NotFound -> {
                Toast.makeText(requireContext(), "找不到 QR Code", Toast.LENGTH_SHORT).show()
            }
            is QrScanResult.Error -> {
                Toast.makeText(requireContext(), "掃描失敗", Toast.LENGTH_SHORT).show()
                Logger.e(TAG, "掃描失敗", result.exception)
            }
        }
    }

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri ?: return@registerForActivityResult

            val bitmap = BitmapUtils.loadBitmapFromUri(requireContext(), uri) ?: run {
                Toast.makeText(requireContext(), "無法載入圖片", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            QrImageScanner().scan(bitmap) { result ->
                handleResult(result)
            }
        }

    override fun onPause() {
        super.onPause()
        qrAnalyzer.disableScanning()
        
        // 1. 記錄當前狀態
        wasFlashlightOn = camera?.cameraInfo?.torchState?.value == TorchState.ON
        
        // 2. 關鍵修正：主動解除所有相機 UseCase 綁定。
        // 在 ViewPager2 中，這能強制釋放相機資源並關閉閃光燈，避免單純呼叫 enableTorch(false) 被系統緩存忽略。
        cameraProvider?.unbindAll()
    }

    override fun onResume() {
        super.onResume()
        qrAnalyzer.enableScanning()
        startCamera()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 銷毀 View 時清除相機引用
        camera = null
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

}
