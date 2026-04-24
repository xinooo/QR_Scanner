package com.xinooo.qrcode.core.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xinooo.qrcode.core.base.BaseFragment
import com.xinooo.qrcode.core.navigation.NavConfig

class BasePagerAdapter(
    activity: FragmentActivity,
    val list: List<NavConfig>,
    val listener: ShowFragmentListener
): FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {
        return listener.showFragment(list[position].id, position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun interface ShowFragmentListener {
        fun showFragment(id: Int, position: Int): BaseFragment<*>
    }
}