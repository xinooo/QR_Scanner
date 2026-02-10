package com.xinooo.qrcode.ui

import android.view.Menu
import com.google.android.material.navigation.NavigationBarView
import com.xinooo.qrcode.core.base.BaseActivity
import com.xinooo.qrcode.R
import com.xinooo.qrcode.core.adapter.BasePagerAdapter
import com.xinooo.qrcode.core.navigation.BottomNavViewPagerMediator
import com.xinooo.qrcode.core.navigation.NavManager
import com.xinooo.qrcode.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val navList = NavManager.getNavList()
    private val navMediator by lazy { BottomNavViewPagerMediator(binding.bottomNav, binding.viewPager, navList) }

    override fun getLayoutId() = R.layout.activity_main

    override fun initLayoutView() {
        createBottomNav()
        binding.viewPager.adapter = BasePagerAdapter(this, navList
        ) { tag, position ->
            return@BasePagerAdapter QRCodeScannerFragment()
        }
        navMediator.attach()
    }

    override fun initViewData() {}

    fun createBottomNav() {
        binding.bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
        val menu = binding.bottomNav.menu
        navList.forEachIndexed { index, item ->
            menu.add(Menu.NONE, item.id, index, item.title)
                .setIcon(item.iconRes)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        navMediator.detach()
    }
}