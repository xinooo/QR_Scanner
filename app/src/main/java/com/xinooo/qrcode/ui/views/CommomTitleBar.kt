package com.xinooo.qrcode.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import com.xinooo.qrcode.databinding.CommomTitleBarBinding

/**
 * APP 標題列
 */
class CommomTitleBar(context: Context, attributeSet: AttributeSet): LinearLayout(context, attributeSet) {
    private val binding: CommomTitleBarBinding

    init {
        binding = CommomTitleBarBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setAppTitle(title: CharSequence) {
        binding.appTitle.text = title
    }

    fun setAppIcon(@DrawableRes iconRes: Int) {
        binding.appIcon.setImageResource(iconRes)
    }

}
