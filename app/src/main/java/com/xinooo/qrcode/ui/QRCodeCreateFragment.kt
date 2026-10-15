package com.xinooo.qrcode.ui

import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import com.xinooo.qrcode.R
import com.xinooo.qrcode.core.base.BaseFragment
import com.xinooo.qrcode.databinding.FragmentQrcodeCreateBinding
import com.xinooo.qrcode.utils.AdManager
import com.xinooo.qrcode.utils.BitmapUtils
import com.xinooo.qrcode.utils.FileUtils

class QRCodeCreateFragment : BaseFragment<FragmentQrcodeCreateBinding>() {

    private var currentBitmap: Bitmap? = null

    override fun getLayoutId(): Int = R.layout.fragment_qrcode_create

    override fun initLayoutView() {
        binding.btnGenerate.setOnClickListener {
            val content = binding.etContent.text.toString()
            if (content.isNotEmpty()) {
                val bitmap = BitmapUtils.createQrCodeBitmap(requireContext(), content)
                    ?: return@setOnClickListener
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
        binding.titleBar.setAppIcon(R.drawable.ic_nav_create)
    }

}
