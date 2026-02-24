package com.xinooo.qrcode.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.xinooo.qrcode.app.scaleIfNeeded

object BitmapUtils {

    fun loadBitmapFromUri(context: Context, uri: Uri, maxSize: Int = 1280): Bitmap? {
        return try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.isMutableRequired = true
                }
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
            bitmap.scaleIfNeeded(maxSize)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}