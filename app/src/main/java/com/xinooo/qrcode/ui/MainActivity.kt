package com.xinooo.qrcode.ui

import com.xinooo.qrcode.core.base.BaseActivity
import com.xinooo.qrcode.R
import com.xinooo.qrcode.core.adapter.BasePagerAdapter
import com.xinooo.qrcode.core.navigation.BottomNavViewPagerMediator
import com.xinooo.qrcode.core.navigation.NavManager
import com.xinooo.qrcode.databinding.ActivityMainBinding
import com.xinooo.qrcode.ui.history.ScanHistoryFragment
import com.xinooo.qrcode.ui.scan.QRCodeScannerFragment
import com.xinooo.qrcode.ui.setting.SettingsFragment

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val navList = NavManager.getNavList()
    private val navItems by lazy {
        listOf(binding.navScanner, binding.navCreate, binding.navHistory, binding.navSettings)
    }
    private val navMediator by lazy {
        BottomNavViewPagerMediator(navItems, binding.viewPager, navList)
    }

    override fun getLayoutId() = R.layout.activity_main

    override fun initLayoutView() {
        binding.viewPager.adapter = BasePagerAdapter(this, navList
        ) { id, position ->
            return@BasePagerAdapter when(id) {
                NavManager.NavId.HOME -> QRCodeScannerFragment()
                NavManager.NavId.CREATE -> QRCodeCreateFragment()
                NavManager.NavId.HISTORY -> ScanHistoryFragment()
                NavManager.NavId.PROFILE -> SettingsFragment()
                else -> QRCodeScannerFragment()
            }
        }
        navMediator.attach()
    }

    override fun initViewData() {}

    override fun onDestroy() {
        super.onDestroy()
        navMediator.detach()
    }
}
