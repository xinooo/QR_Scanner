package com.xinooo.qrcode.core.navigation

import com.xinooo.qrcode.R
import com.xinooo.qrcode.app.AppMain

object NavManager {

    object NavId {
        const val HOME = 0
        const val ORDER = 1
        const val MESSAGE = 2
        const val PROFILE = 3
    }

    fun getNavList(): List<NavConfig> {
        return listOf(
            NavConfig(NavId.HOME, AppMain.getAppString(R.string.nav_scanner), android.R.drawable.ic_menu_camera),
            NavConfig(NavId.ORDER, AppMain.getAppString(R.string.nav_create), android.R.drawable.ic_menu_add),
            NavConfig(NavId.MESSAGE, AppMain.getAppString(R.string.nav_history), android.R.drawable.ic_menu_recent_history),
            NavConfig(NavId.PROFILE, AppMain.getAppString(R.string.nav_setting), android.R.drawable.ic_menu_preferences)
        )
    }
}