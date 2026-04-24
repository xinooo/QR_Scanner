package com.xinooo.qrcode.ui.history

import androidx.lifecycle.lifecycleScope
import com.webrtc.cc.ui.BaseFragment
import com.xinooo.qrcode.R
import com.xinooo.qrcode.data.QrCodeScanResultRepository
import com.xinooo.qrcode.databinding.FragmentScanHistoryBinding
import com.xinooo.qrcode.utils.AdManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ScanHistoryFragment : BaseFragment<FragmentScanHistoryBinding>() {

    private val repository by lazy { QrCodeScanResultRepository(requireContext()) }
    private val scanResultAdapter = ScanHistoryAdapter()

    override fun getLayoutId(): Int = R.layout.fragment_scan_history

    override fun initLayoutView() {
        binding.titleBar.setAppTitle(getString(R.string.nav_history))
        binding.titleBar.setLeftBtnVisibility(false)
        binding.recyclerView.adapter = scanResultAdapter

        scanResultAdapter.onItemClick = { result ->
            val dialog = ScanHistoryDetailDialog.newInstance(result)
            dialog.show(childFragmentManager, "HistoryDetail")
        }

        // Load Ad
        AdManager.loadBannerAd(binding.adView)
    }

    override fun initViewData() {
        lifecycleScope.launch {
            repository.getAllScanResults().collectLatest {
                scanResultAdapter.submitList(it)
            }
        }
    }
}