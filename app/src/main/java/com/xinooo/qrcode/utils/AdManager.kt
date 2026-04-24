package com.xinooo.qrcode.utils

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

object AdManager {
    /**
     * 載入橫幅廣告
     */
    fun loadBannerAd(adView: AdView?) {
        adView?.let {
            val adRequest = AdRequest.Builder().build()
            it.loadAd(adRequest)
        }
    }
}
