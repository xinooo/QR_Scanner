package com.xinooo.qrcode.app

import android.app.Activity
import java.util.LinkedList

//Activity 管理類
object ActivitiesManager {
    val activityStack = LinkedList<Activity>()

    //刪除頂部 Activity
    fun popActivity() {
        activityStack.removeFirstOrNull()?.finish()
    }

    //刪除指定 Activity
    fun popActivity(activity: Activity) {
        activityStack.remove(activity)
    }

    fun popActivity(className: String) {
        val iterator = activityStack.iterator()
        while (iterator.hasNext()) {
            val activity = iterator.next()
            if (activity.javaClass.simpleName == className) {
                activity.finish()
            }
        }
    }

    //獲取頂部 Activity
    fun currentActivity(): Activity? {
        val iterator = activityStack.iterator()
        while (iterator.hasNext()) {
            val activity = iterator.next()
            if (isValidActivity(activity)) {
                return activity
            } else {
                iterator.remove()
            }
        }
        return null
    }

    //向堆棧中添加 Activity
    fun pushActivity(activity: Activity) {
        if (isStackActivity(activity)) {
            activityStack.remove(activity)
        }
        activityStack.addFirst(activity)
    }

    //判斷 Activity 是否在堆棧中
    fun isStackActivity(activity: Activity) = activityStack.contains(activity)

    //判斷 Activity 是否在堆棧中
    fun isStackActivity(className: String): Boolean {
        val iterator = activityStack.iterator()
        while (iterator.hasNext()) {
            val activity = iterator.next()
            if (activity.javaClass.simpleName == className) {
                return true
            }
        }
        return false
    }

    //判斷 Activity 是否有效
    private fun isValidActivity(activity: Activity) =
        !(activity.isFinishing || activity.isDestroyed)

    //退出堆棧中所有 Activity 到指定 Activity
    fun popAllActivityExceptOne(className: String) {
        val iterator = activityStack.iterator()
        while (iterator.hasNext()) {
            val activity = iterator.next()
            if (activity.javaClass.simpleName != className) {
                iterator.remove()
                activity.finish()
            }
        }
    }

    //退出堆棧中所有 Activity
    fun finishAllActivity() = popAllActivityExceptOne("")


}