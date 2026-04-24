package com.xinooo.qrcode.ui

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.xinooo.qrcode.core.base.BaseFragment
import com.xinooo.qrcode.R
import com.xinooo.qrcode.databinding.FragmentQrcodeCreateBinding
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.xinooo.qrcode.utils.AdManager

import android.content.Intent
import android.view.View
import com.xinooo.qrcode.utils.FileUtils

class QRCodeCreateFragment : BaseFragment<FragmentQrcodeCreateBinding>() {

    private var currentBitmap: Bitmap? = null

    override fun getLayoutId(): Int = R.layout.fragment_qrcode_create

    override fun initLayoutView() {
        binding.btnGenerate.setOnClickListener {
            val content = binding.etContent.text.toString()
            if (content.isNotEmpty()) {
                val bitmap = generateQRCode(content)
                currentBitmap = bitmap
                binding.ivQRCode.setImageBitmap(bitmap)
                binding.btnShare.visibility = View.VISIBLE
            }
        }

        binding.btnShare.setOnClickListener {
            shareQRCode()
        }
        // Load Ad
        AdManager.loadBannerAd(binding.adView)
    }

    private fun shareQRCode() {
        currentBitmap?.let { bitmap ->
            val uri = FileUtils.getShareImageUri(requireContext(), bitmap)
            uri?.let { shareUri ->
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, shareUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)))
            }
        }
    }

    override fun initViewData() {
        binding.titleBar.setAppTitle(getString(R.string.nav_create))
        binding.titleBar.setLeftBtnVisibility(false)
    }

    private fun generateQRCode(content: String): Bitmap {
        val hints = hashMapOf<EncodeHintType, Any>(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
            EncodeHintType.MARGIN to 1
        )
        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, 512, 512, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        return bitmap
    }
}