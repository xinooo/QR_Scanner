package com.xinooo.qrcode.ui.history

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.xinooo.qrcode.R
import com.xinooo.qrcode.core.base.BaseDialogFragment
import com.xinooo.qrcode.data.QrCodeScanResult
import com.xinooo.qrcode.databinding.DialogHistoryDetailBinding
import com.xinooo.qrcode.utils.BitmapUtils

class ScanHistoryDetailDialog : BaseDialogFragment<DialogHistoryDetailBinding>() {

    private var scanResult: QrCodeScanResult? = null

    companion object {
        private const val ARG_RESULT = "arg_result"

        fun newInstance(result: QrCodeScanResult): ScanHistoryDetailDialog {
            val args = Bundle().apply {
                putSerializable(ARG_RESULT, result)
            }
            val fragment = ScanHistoryDetailDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.dialog_history_detail

    override fun initLayoutView() {
        setWidthPercent(0.85f)

        binding.btnCopy.setOnClickListener {
            copyToClipboard(scanResult?.result ?: "")
            Toast.makeText(requireContext(), R.string.scan_result_copied, Toast.LENGTH_SHORT).show()
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun initViewData() {
        @Suppress("DEPRECATION")
        scanResult = arguments?.getSerializable(ARG_RESULT) as? QrCodeScanResult
        binding.result = scanResult

        scanResult?.let {
            val bitmap = BitmapUtils.createQrCodeBitmap(it.result)
            binding.ivQrCode.setImageBitmap(bitmap)
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(getString(R.string.scan_result_clipboard_label), text)
        clipboard.setPrimaryClip(clip)
    }
}