package com.xinooo.qrcode.data

import android.content.Context
import com.google.gson.Gson
import com.xinooo.qrcode.utils.FileUtils
import com.xinooo.qrcode.utils.Logger

object SettingsManager {
    private const val SETTINGS_FILE_NAME = "settings.json"
    private val gson = Gson()
    private var currentConfig: SettingsConfig? = null

    /**
     * 載入設定：比對 Assets 與本地檔案的版本
     */
    fun loadSettings(context: Context): SettingsConfig {
        // 1. 讀取 Assets
        val assetsJson = FileUtils.getAssetsData(context, SETTINGS_FILE_NAME)
        val assetsConfig = gson.fromJson(assetsJson, SettingsConfig::class.java)

        // 2. 讀取本地
        val localJson = FileUtils.readFile(context, SETTINGS_FILE_NAME)
        
        val finalConfig = if (localJson == null) {
            Logger.i("SettingsManager", "Local settings not found, using assets.")
            assetsConfig
        } else {
            val localConfig = gson.fromJson(localJson, SettingsConfig::class.java)
            if (assetsConfig.root > localConfig.root) {
                Logger.i("SettingsManager", "Assets version (${assetsConfig.root}) is newer than local (${localConfig.root}). Updating.")
                assetsConfig
            } else {
                Logger.i("SettingsManager", "Using local settings.")
                localConfig
            }
        }
        
        currentConfig = finalConfig
        return finalConfig
    }

    /**
     * 更新記憶體中的設定
     */
    fun updateItem(key: String, isCheck: Boolean) {
        currentConfig?.data?.find { it.key == key }?.isCheck = isCheck
    }

    /**
     * 將設定儲存至本地外部空間
     */
    fun saveToLocal(context: Context) {
        currentConfig?.let {
            val json = gson.toJson(it)
            val success = FileUtils.writeFile(context, SETTINGS_FILE_NAME, json)
            if (success) {
                Logger.i("SettingsManager", "Settings saved to local storage.")
            } else {
                Logger.e("SettingsManager", "Failed to save settings.")
            }
        }
    }

    fun getConfig(): SettingsConfig? = currentConfig
}
