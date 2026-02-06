package com.xinooo.qrcode.app

import android.content.Context

fun Context.getStatusBarHeight(): Int {
    val resourceId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        this.resources.getDimensionPixelSize(resourceId)
    } else {
        0
    }
}