package com.xinooo.qrcode.app

import android.app.Application
import android.os.Handler
import android.os.Looper

class AppMain: Application() {

    private val mHandler = Handler(Looper.getMainLooper())


    companion object {
        private lateinit var app: AppMain
        fun getApp() = app

        fun getAppString(resId: Int): String {
            ActivitiesManager.currentActivity()?.let {
                return it.getString(resId)
            } ?: run {
                return getApp().getString(resId)
            }
        }

        fun getAppString(resId: Int, vararg formatArgs: Any?): String {
            ActivitiesManager.currentActivity()?.let {
                return it.getString(resId, *formatArgs)
            } ?: run {
                return getApp().getString(resId, *formatArgs)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }

    fun getHandler() = mHandler

}