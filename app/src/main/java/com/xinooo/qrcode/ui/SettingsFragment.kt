package com.xinooo.qrcode.ui

import com.webrtc.cc.ui.BaseFragment
import com.xinooo.qrcode.R
import com.xinooo.qrcode.data.SettingsManager
import com.xinooo.qrcode.databinding.FragmentSettingsBinding
import com.xinooo.qrcode.utils.AdManager

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    private val adapter by lazy {
        SettingsAdapter(emptyList()) { key, isChecked ->
            SettingsManager.updateItem(key, isChecked)
        }
    }

    override fun getLayoutId() = R.layout.fragment_settings

    override fun initLayoutView() {
        binding.titleBar.setAppTitle(getString(R.string.nav_setting))
        binding.titleBar.setLeftBtnVisibility(false)
        binding.rvSettings.adapter = adapter

        // Load Ad
        AdManager.loadBannerAd(binding.adView)
    }

    override fun initViewData() {
        val config = SettingsManager.loadSettings(requireContext())
        adapter.updateData(config.data)
    }

    override fun onPause() {
        super.onPause()
        // 離開頁面時更新至本地外部私有空間
        SettingsManager.saveToLocal(requireContext())
    }
}
