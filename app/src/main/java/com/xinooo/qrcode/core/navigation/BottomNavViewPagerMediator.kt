package com.xinooo.qrcode.core.navigation

import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton

class BottomNavViewPagerMediator(
    private val navItems: List<MaterialButton>,
    private val viewPager: ViewPager2,
    private val navList: List<NavConfig>
) {
    init {
        require(navItems.size == navList.size) {
            "Bottom navigation item count must match the page count."
        }
    }

    private val pageCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            selectItem(position)
        }
    }

    fun attach() {
        viewPager.registerOnPageChangeCallback(pageCallback)
        navItems.forEachIndexed { index, item ->
            item.text = navList[index].title
            item.setIconResource(navList[index].iconRes)
            item.setOnClickListener {
                selectItem(index)
                if (viewPager.currentItem != index) {
                    viewPager.currentItem = index
                }
            }
        }
        selectItem(viewPager.currentItem)
    }

    fun detach() {
        viewPager.unregisterOnPageChangeCallback(pageCallback)
        navItems.forEach { it.setOnClickListener(null) }
    }

    private fun selectItem(selectedPosition: Int) {
        navItems.forEachIndexed { index, item ->
            item.isChecked = index == selectedPosition
        }
    }
}
