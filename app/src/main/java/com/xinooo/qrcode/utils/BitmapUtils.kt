package com.xinooo.qrcode.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.ColorInt
import androidx.core.graphics.toColorInt
import com.xinooo.qrcode.R
import com.xinooo.qrcode.app.scaleIfNeeded
import qrcode.QRCode
import qrcode.raw.ErrorCorrectionLevel
import qrcode.raw.QRCodeProcessor
import qrcode.render.QRCodeGraphics
import java.io.ByteArrayOutputStream

object BitmapUtils {

    private const val QR_CODE_SIZE = 1024
    private const val QUIET_ZONE_MODULES = 1
    private const val CENTER_ICON_SIZE = 256

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

    /**
     * 生成 QR Code Bitmap
     * 預設中央圖示來源：Stitch screen 8eb29000c1fb4065b6931bf97348b460。
     */
    fun createQrCodeBitmap(
        context: Context,
        content: String,
        @ColorInt color: Int = "#00639C".toColorInt(),
        centerIcon: Bitmap? = defaultCenterIcon(context)
    ): Bitmap? {
        return try {
            val informationDensity = QRCodeProcessor.infoDensityForDataAndECL(
                content,
                ErrorCorrectionLevel.HIGH
            )
            // informationDensity 代表 QR Version（1..40）。
            // 每邊 module 數量公式：17 + (QR Version × 4)，例如 Version 1 為 21×21。
            val moduleCount = informationDensity * 4 + 17
            val maxCellSize = QR_CODE_SIZE / (moduleCount + QUIET_ZONE_MODULES * 2)
            val cellSize = (maxCellSize - maxCellSize % 2).coerceAtLeast(2)
            val qrContentSize = cellSize * moduleCount
            val offset = (QR_CODE_SIZE - qrContentSize) / 2
            val builder = QRCode.ofCircles()
                .withColor(color)
                .withBackgroundColor(Color.WHITE)
                .withInnerSpacing(0)
                .withErrorCorrectionLevel(ErrorCorrectionLevel.HIGH)
                .withInformationDensity(informationDensity)
                .withSize(cellSize)
                .withCanvasSize(qrContentSize)
                .withXOffset(offset)
                .withYOffset(offset)

            if (centerIcon != null) {
                builder.withLogo(
                    logo = centerIcon.toPngByteArray(CENTER_ICON_SIZE),
                    width = CENTER_ICON_SIZE,
                    height = CENTER_ICON_SIZE,
                    clearLogoArea = true
                )
            }

            val output = QRCodeGraphics(QR_CODE_SIZE, QR_CODE_SIZE).apply {
                fill(Color.WHITE)
            }
            builder.build(content)
                .render(output)
                .nativeImage() as Bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun Bitmap.toPngByteArray(size: Int): ByteArray {
        val scaledBitmap = Bitmap.createScaledBitmap(this, size, size, true)
        return try {
            ByteArrayOutputStream().use { output ->
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                output.toByteArray()
            }
        } finally {
            if (scaledBitmap !== this) {
                scaledBitmap.recycle()
            }
        }
    }

    private fun defaultCenterIcon(context: Context) = BitmapFactory.decodeResource(
        context.resources,
        R.mipmap.qr_center_cloud,
        BitmapFactory.Options().apply {
            inScaled = false
        }
    )
}
