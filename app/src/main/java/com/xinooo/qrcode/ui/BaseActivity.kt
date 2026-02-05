package com.xinooo.qrcode.ui

import android.Manifest
import android.graphics.Rect
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.xinooo.qrcode.R
import com.xinooo.qrcode.app.ActivitiesManager
import com.xinooo.qrcode.utils.Logger
import com.xinooo.qrcode.utils.PermissionHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseActivity<T: ViewDataBinding>: AppCompatActivity() {
    protected val TAG = this.javaClass.simpleName

    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            if (v.getTag(R.id.tag_initial_padding) == null) {
                v.setTag(R.id.tag_initial_padding,
                    Rect(v.paddingLeft, v.paddingTop, v.paddingRight, v.paddingBottom)
                )
            }
            val originalPadding = v.getTag(R.id.tag_initial_padding) as Rect
            v.setPadding(originalPadding.left + systemBars.left,
                originalPadding.top + 0,
                originalPadding.right + systemBars.right,
                originalPadding.bottom + systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }
        Logger.i(TAG, "onCreate")
        setStatusBarTextColor(true)
        ActivitiesManager.pushActivity(this)
        setAcitivityParam()
        requestPermission()
    }

    private fun binding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId())
    }

    protected open fun setAcitivityParam() {}
    protected abstract fun getLayoutId(): Int
    protected abstract fun initLayoutView()
    protected abstract fun initViewData()
    protected open fun registerFlowBus(){}

    protected open fun requestPermission() {
        PermissionHelper.from(this)
            .request(
                arrayOf(Manifest.permission.CAMERA)
            ) { granted, deniedList ->
                if (granted) {
                    Logger.i(TAG, "granted")
                    initLayoutView()
                    initViewData()
                    registerFlowBus()
                } else {
                    finish()
                }
            }
    }

    //可供子類簡化 launch 使用
    protected fun launchCollect(
        dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
        block: suspend () -> Unit) {
        lifecycleScope.launch(dispatcher) {
            block()
        }
    }

    /**
     * 手動切換狀態列文字顏色深淺
     * @param isDark true：深色，false：淺色
     */
    protected fun setStatusBarTextColor(isDark: Boolean) {
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = isDark
    }

    override fun onStart() {
        super.onStart()
        Logger.i(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Logger.i(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Logger.i(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Logger.i(TAG, "onStop")
    }

    override fun finish() {
        super.finish()
        Logger.i(TAG, "finish")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.i(TAG, "onDestroy")
        ActivitiesManager.popActivity(this)
    }

    fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.let {
            if (it.isActive) {
                val windowToken = window.decorView.windowToken
                if (windowToken != null) {
                    it.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                }
            }
        }
    }

}