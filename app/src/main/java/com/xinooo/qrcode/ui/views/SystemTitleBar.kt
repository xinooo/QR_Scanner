package com.xinooo.qrcode.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.xinooo.qrcode.app.getStatusBarHeight
import com.xinooo.qrcode.databinding.SystemTitleBarBinding

/**
 * 系統狀態欄列
 */
class SystemTitleBar(context: Context, attributeSet: AttributeSet): LinearLayout(context, attributeSet) {

    val binding: SystemTitleBarBinding

    init {
        binding = SystemTitleBarBinding.inflate(LayoutInflater.from(context), this, true).apply {
            systemTitleBar.layoutParams.height = context.getStatusBarHeight()
        }
    }

    fun setColor(color: Int) {
        binding.systemTitleBar.setBackgroundColor(color)
    }

}