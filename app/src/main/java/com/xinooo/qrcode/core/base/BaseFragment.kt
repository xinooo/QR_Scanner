package com.webrtc.cc.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.xinooo.qrcode.utils.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseFragment<T : ViewDataBinding>: Fragment() {
    protected val TAG = this.javaClass.simpleName

    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.i(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        Logger.i(TAG, "onCreateView")
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        initParams()
        initLayoutView()
        initViewData()
        registerFlowBus()
        return binding.root
    }

    protected open fun initParams(){}
    protected abstract fun getLayoutId(): Int
    protected abstract fun initLayoutView()
    protected abstract fun initViewData()
    protected open fun registerFlowBus(){}

    //可供子類簡化 launch 使用
    protected fun launchCollect(
        dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
        block: suspend () -> Unit) {
        lifecycleScope.launch(dispatcher) {
            block()
        }
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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Logger.i(TAG, "onHiddenChanged: $hidden")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.i(TAG, "onDestroy")
    }

}