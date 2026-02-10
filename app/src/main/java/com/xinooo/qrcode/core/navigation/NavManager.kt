package com.xinooo.qrcode.core.navigation

object NavManager {

    object NavId {
        const val HOME = 0
        const val ORDER = 1
        const val MESSAGE = 2
        const val PROFILE = 3
    }

    fun getNavList(): List<NavConfig> {
        return listOf(
            NavConfig(NavId.HOME, "掃描", android.R.drawable.ic_menu_camera),
            NavConfig(NavId.ORDER, "建立", android.R.drawable.ic_menu_add),
            NavConfig(NavId.MESSAGE, "紀錄", android.R.drawable.ic_menu_recent_history),
            NavConfig(NavId.PROFILE, "設定", android.R.drawable.ic_menu_preferences)
        )
    }
}