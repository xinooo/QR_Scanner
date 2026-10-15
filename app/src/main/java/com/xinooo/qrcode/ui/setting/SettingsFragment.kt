package com.xinooo.qrcode.ui.setting

import androidx.core.content.pm.PackageInfoCompat
import com.xinooo.qrcode.R
import com.xinooo.qrcode.core.base.BaseFragment
import com.xinooo.qrcode.data.SettingsManager
import com.xinooo.qrcode.databinding.FragmentSettingsBinding
import com.xinooo.qrcode.utils.AdManager

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    private val adapter by lazy {
        SettingsAdapter(emptyList()) { key, isChecked ->
            SettingsManager.updateItem(key, isChecked)
            updateAppliedCount()
        }
    }

    override fun getLayoutId() = R.layout.fragment_settings

    override fun initLayoutView() {
        binding.titleBar.setAppTitle(getString(R.string.nav_setting))
        binding.titleBar.setAppIcon(R.drawable.ic_nav_profile)
        binding.rvSettings.adapter = adapter
        bindAppInfo()

        // Load Ad
        AdManager.loadBannerAd(binding.adView)
    }

    override fun initViewData() {
        val config = SettingsManager.loadSettings(requireContext())
        adapter.updateData(config.data)
        updateAppliedCount()
    }

    private fun bindAppInfo() {
        val context = requireContext()
        val packageManager = context.packageManager
        val applicationInfo = context.applicationInfo
        val packageInfo = packageManager.getPackageInfo(context.packageName, 0)

        binding.ivAppIcon.setImageDrawable(applicationInfo.loadIcon(packageManager))
        binding.tvAppName.text = applicationInfo.loadLabel(packageManager)
        binding.tvAppVersion.text = getString(
            R.string.settings_version,
            packageInfo.versionName.orEmpty(),
            PackageInfoCompat.getLongVersionCode(packageInfo)
        )
    }

    private fun updateAppliedCount() {
        val appliedCount = SettingsManager.getConfig()?.data?.count { it.isCheck } ?: 0
        binding.tvAppliedCount.text = getString(R.string.settings_applied_count, appliedCount)
    }

    override fun onPause() {
        super.onPause()
        // 離開頁面時更新至本地外部私有空間
        SettingsManager.saveToLocal(requireContext())
    }
}
