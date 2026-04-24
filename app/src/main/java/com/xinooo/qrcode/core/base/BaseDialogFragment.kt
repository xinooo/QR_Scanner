package com.xinooo.qrcode.core.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment

abstract class BaseDialogFragment<T : ViewDataBinding> : DialogFragment() {

    protected lateinit var binding: T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 去除標題列
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        // 背景透明，以便顯示自定義圓角背景
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayoutView()
        initViewData()
    }

    protected abstract fun getLayoutId(): Int
    protected abstract fun initLayoutView()
    protected abstract fun initViewData()

    /**
     * 設定 Dialog 寬度為螢幕比例
     */
    protected fun setWidthPercent(percent: Float = 0.85f) {
        val width = (resources.displayMetrics.widthPixels * percent).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
