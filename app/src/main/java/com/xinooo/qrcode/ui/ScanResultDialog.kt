package com.xinooo.qrcode.ui

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.xinooo.qrcode.R
import com.xinooo.qrcode.databinding.DialogScanResultBinding

class ScanResultDialog : DialogFragment() {

    private var onDismissListener: (() -> Unit)? = null

    companion object {
        private const val ARG_RESULT = "arg_result"

        /**
         * 建立 ScanResultDialog 實例
         * @param result 掃描內容
         * @param onDismiss 彈窗關閉後的回呼（用於重啟掃描）
         */
        fun newInstance(result: String, onDismiss: () -> Unit): ScanResultDialog {
            val fragment = ScanResultDialog()
            val args = Bundle().apply {
                putString(ARG_RESULT, result)
            }
            fragment.arguments = args
            fragment.onDismissListener = onDismiss
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val result = arguments?.getString(ARG_RESULT) ?: ""
        val isUrl = result.startsWith("http://", ignoreCase = true) ||
                    result.startsWith("https://", ignoreCase = true)

        val binding = DialogScanResultBinding.inflate(LayoutInflater.from(requireContext()))
        
        binding.tvScanContent.text = result

        binding.btnCopy.setOnClickListener {
            copyToClipboard(result)
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        if (isUrl) {
            binding.btnOpenWeb.visibility = View.VISIBLE
            binding.btnOpenWeb.setOnClickListener {
                openWebPage(result)
                dismiss()
            }
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
        
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(getString(R.string.scan_result_clipboard_label), text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), getString(R.string.scan_result_copied), Toast.LENGTH_SHORT).show()
    }

    private fun openWebPage(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), getString(R.string.scan_result_open_error), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // 當使用者點擊關閉或開啟網頁後彈窗消失，觸發回呼
        onDismissListener?.invoke()
    }
}
