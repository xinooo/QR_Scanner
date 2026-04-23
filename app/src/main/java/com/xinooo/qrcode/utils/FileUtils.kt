package com.xinooo.qrcode.utils

import android.content.Context
import java.io.File
import java.io.IOException

object FileUtils {

    /**
     * 從 Assets 中讀取文字資料
     * @param context 上下文
     * @param fileName 檔案名稱 (例如 "data.json")
     * @return 檔案內容字串，若失敗則回傳空字串
     */
    fun getAssetsData(context: Context, fileName: String): String {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            Logger.e("FileUtils", "Error reading assets: $fileName", e)
            ""
        }
    }

    /**
     * 從應用程式外部私有目錄 (getExternalFilesDir) 讀取檔案
     * @param context 上下文
     * @param subPath 子路徑或檔案名稱
     * @return 檔案內容字串，若檔案不存在或讀取失敗則回傳 null
     */
    fun readFile(context: Context, subPath: String): String? {
        val file = File(context.getExternalFilesDir(null), subPath)
        return if (file.exists()) {
            try {
                file.readText()
            } catch (e: IOException) {
                Logger.e("FileUtils", "Error reading file: ${file.absolutePath}", e)
                null
            }
        } else {
            Logger.w("FileUtils", "File not found: ${file.absolutePath}")
            null
        }
    }

    /**
     * 寫入內容至應用程式外部私有目錄 (getExternalFilesDir)
     * @param context 上下文
     * @param subPath 子路徑或檔案名稱
     * @param content 要寫入的內容
     * @return 是否寫入成功
     */
    fun writeFile(context: Context, subPath: String, content: String): Boolean {
        val file = File(context.getExternalFilesDir(null), subPath)
        return try {
            // 確保父目錄存在
            file.parentFile?.let {
                if (!it.exists()) it.mkdirs()
            }
            file.writeText(content)
            true
        } catch (e: IOException) {
            Logger.e("FileUtils", "Error writing file: ${file.absolutePath}", e)
            false
        }
    }

    /**
     * 取得應用程式外部私有目錄的絕對路徑
     */
    fun getExternalPath(context: Context): String {
        return context.getExternalFilesDir(null)?.absolutePath ?: ""
    }
}
