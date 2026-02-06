package com.xinooo.qrcode.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.xinooo.qrcode.app.ActivitiesManager
import com.xinooo.qrcode.databinding.CommomTitleBarBinding

/**
 * APP 標題列
 */
class CommomTitleBar(context: Context, attributeSet: AttributeSet): LinearLayout(context, attributeSet) {
    private val binding: CommomTitleBarBinding

    init {
        binding = CommomTitleBarBinding.inflate(LayoutInflater.from(context), this, true).apply {
            ivLeftBtn.setOnClickListener {
                ActivitiesManager.currentActivity()?.finish()
            }
        }
    }

    fun setAppTitle(title: CharSequence) {
        binding.appTitle.text = title
    }

    fun setDividerVisibility(visibility: Boolean) {
        binding.titleDivider.visibility = if (visibility) VISIBLE else GONE
    }

    fun setLeftBtnVisibility(visibility: Boolean) {
        binding.ivLeftBtn.visibility = if (visibility) VISIBLE else GONE
    }

}