package com.xinooo.qrcode.data

data class SettingsConfig(
    val root: Int,
    val data: List<SettingItem>
)

data class SettingItem(
    val key: String,
    val id: String,
    val note: String,
    var isCheck: Boolean
)
