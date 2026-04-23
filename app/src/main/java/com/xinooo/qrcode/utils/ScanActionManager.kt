package com.xinooo.qrcode.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.widget.Toast
import com.xinooo.qrcode.R
import com.xinooo.qrcode.data.SettingsManager

object ScanActionManager {

    /**
     * 執行掃描後的自動化動作
     */
    fun executeActions(context: Context, result: String) {
        val config = SettingsManager.getConfig() ?: SettingsManager.loadSettings(context)

        // 1. 播放提示音
        val isSoundEnabled = config.data.find { it.key == "sound" }?.isCheck == true
        if (isSoundEnabled) {
            playBeepSound()
        }

        // 2. 自動複製到剪貼簿
        val isClipboardEnabled = config.data.find { it.key == "clipboard" }?.isCheck == true
        if (isClipboardEnabled) {
            copyToClipboard(context, result)
        }

        // 3. 自動打開網頁
        val isUrl = result.startsWith("http://", ignoreCase = true) || 
                    result.startsWith("https://", ignoreCase = true)
        val isOpenWebEnabled = config.data.find { it.key == "openweb" }?.isCheck == true
        if (isUrl && isOpenWebEnabled) {
            openWebPage(context, result)
        }
    }

    private fun playBeepSound() {
        try {
            val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
            tg.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
        } catch (e: Exception) {
            Logger.e("ScanActionManager", "Failed to play beep sound", e)
        }
    }

    private fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val label = context.getString(R.string.scan_result_clipboard_label)
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, context.getString(R.string.scan_result_copied), Toast.LENGTH_SHORT).show()
    }

    private fun openWebPage(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Logger.e("ScanActionManager", "Failed to open URL: $url", e)
        }
    }
}
