package com.xinooo.qrcode.utils

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * 權限請求幫助類
 */
class PermissionHelper(
    private val requestPermissionLauncher: ActivityResultLauncher<Array<String>>
) {
    private var onResult: ((granted: Boolean, deniedList: List<String>) -> Unit)? = null

    fun request(
        permissions: Array<String>,
        onResult: (granted: Boolean, deniedList: List<String>) -> Unit
    ) {
        this.onResult = onResult
        requestPermissionLauncher.launch(permissions)
    }

    private fun handleResult(result: Map<String, Boolean>) {
        val denied = result.filterValues { !it }.keys.toList()
        val granted = denied.isEmpty()
        onResult?.invoke(granted, denied)
    }

    companion object {
        // 建立在 Activity 使用的實例
        fun from(activity: AppCompatActivity): PermissionHelper {
            lateinit var helper: PermissionHelper
            val launcher = activity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { result ->
                helper.handleResult(result)
            }
            helper = PermissionHelper(launcher)
            return helper
        }

        // 建立在 Fragment 使用的實例
        fun from(fragment: Fragment): PermissionHelper {
            lateinit var helper: PermissionHelper
            val launcher = fragment.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { result ->
                helper.handleResult(result)
            }
            helper = PermissionHelper(launcher)
            return helper
        }
    }
}
