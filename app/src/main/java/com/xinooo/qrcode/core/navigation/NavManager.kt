package com.xinooo.qrcode.core.navigation

import com.xinooo.qrcode.R
import com.xinooo.qrcode.app.AppMain

object NavManager {

    object NavId {
        const val HOME = 0
        const val CREATE = 1
        const val HISTORY = 2
        const val PROFILE = 3
    }

    fun getNavList(): List<NavConfig> {
        return listOf(
            NavConfig(NavId.HOME, AppMain.getAppString(R.string.nav_scanner), R.drawable.ic_nav_scanner),
            NavConfig(NavId.CREATE, AppMain.getAppString(R.string.nav_create), R.drawable.ic_nav_create),
            NavConfig(NavId.HISTORY, AppMain.getAppString(R.string.nav_history), R.drawable.ic_nav_history),
            NavConfig(NavId.PROFILE, AppMain.getAppString(R.string.nav_setting), R.drawable.ic_nav_profile)
        )
    }
}