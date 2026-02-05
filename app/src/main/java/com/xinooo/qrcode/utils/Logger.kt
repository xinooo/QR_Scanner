package com.xinooo.qrcode.utils

import android.util.Log

//日誌控制類
object Logger {
    private const val TAG = "Logger"

    const val ERROR: Int = 1
    const val WARN: Int = 2
    const val INFO: Int = 3
    const val DEBUG: Int = 4
    const val VERBOS: Int = 5

    private var LOG_LEVEL = 6

    fun disableLogger() {
        LOG_LEVEL = 0
    }

    fun e(msg: String) = e(TAG, msg)

    fun e(tag: String, msg: String) {
        if (LOG_LEVEL > ERROR) {
            Log.e(tag, msg)
        }
    }

    fun e(msg: String, tr: Throwable) {
        if (LOG_LEVEL > ERROR) {
            Log.e(TAG, msg, tr)
        }
    }

    fun e(tag: String, msg: String, tr: Throwable) {
        if (LOG_LEVEL > ERROR) {
            Log.e(tag, msg, tr)
        }
    }

    fun w(msg: String) = w(TAG, msg)

    fun w(tag: String, msg: String) {
        if (LOG_LEVEL > WARN) {
            Log.w(tag, msg)
        }
    }

    fun i(msg: String) = i(TAG, msg)

    fun i(tag: String, msg: String) {
        if (LOG_LEVEL > INFO) {
            Log.i(tag, msg)
        }
    }

    fun d(msg: String) = d(TAG, msg)

    fun d(tag: String, msg: String) {
        if (LOG_LEVEL > DEBUG) {
            Log.d(tag, msg)
        }
    }

    fun v(msg: String) = v(TAG, msg)

    fun v(tag: String, msg: String) {
        if (LOG_LEVEL > VERBOS) {
            Log.v(tag, msg)
        }
    }

}