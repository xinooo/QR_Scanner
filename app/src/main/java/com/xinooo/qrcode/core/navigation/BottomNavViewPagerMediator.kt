package com.xinooo.qrcode.core.navigation

import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavViewPagerMediator(
    private val bottomNav: BottomNavigationView,
    private val viewPager: ViewPager2,
    private val navList: List<NavConfig>
) {
    private val pageCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            bottomNav.selectedItemId = navList[position].id
        }
    }

    fun attach() {
        viewPager.registerOnPageChangeCallback(pageCallback)
        bottomNav.setOnItemSelectedListener { item ->
            val index = navList.indexOfFirst { it.id == item.itemId }
            if (index != -1) viewPager.currentItem = index
            true
        }
    }

    fun detach() {
        viewPager.unregisterOnPageChangeCallback(pageCallback)
        bottomNav.setOnItemSelectedListener(null)
    }
}
