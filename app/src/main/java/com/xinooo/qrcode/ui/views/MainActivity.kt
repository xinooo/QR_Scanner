package com.xinooo.qrcode.ui.views

import android.content.Intent
import com.xinooo.qrcode.ui.BaseActivity
import com.xinooo.qrcode.R
import com.xinooo.qrcode.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun getLayoutId() = R.layout.activity_main

    override fun initLayoutView() {
        startActivity(Intent(this, QRCodeScannerActivity::class.java))
        finish()
    }

    override fun initViewData() {

    }
}