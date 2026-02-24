package com.xinooo.qrcode.app

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.scale

fun Context.getStatusBarHeight(): Int {
    val resourceId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        this.resources.getDimensionPixelSize(resourceId)
    } else {
        0
    }
}

fun Bitmap.scaleIfNeeded(maxSize: Int): Bitmap {
    if (width <= maxSize && height <= maxSize) return this

    val ratio = width.toFloat() / height.toFloat()
    val newWidth: Int
    val newHeight: Int

    if (ratio > 1) {
        newWidth = maxSize
        newHeight = (maxSize / ratio).toInt()
    } else {
        newWidth = (maxSize * ratio).toInt()
        newHeight = maxSize
    }

    return this.scale(newWidth, newHeight)
}