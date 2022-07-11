package com.yuyuko233.notificationSyncHelper.utils

import android.content.Context
import android.content.SharedPreferences

class Config(appCtx: Context) {

    private val sp: SharedPreferences = appCtx.getSharedPreferences("setting", Context.MODE_PRIVATE)

    /**
     * 服务状态
     */
    var serviceStatus: Boolean
        get() {
            return this.sp.getBoolean("serviceStatus", false)
        }
        set(value) {
            this.sp.edit().putBoolean("serviceStatus", value).apply()
        }

    /**
     * 调试模式状态
     */
    var debugMode: Boolean
        get() {
            return this.sp.getBoolean("debugMode", false)
        }
        set(value) {
            this.sp.edit().putBoolean("debugMode", value).apply()
        }

    /**
     * 绑定主机 URL
     */
    var bindHostUrl: String
        get() {
            return this.sp.getString("bindHostUrl", "")!!
        }
        set(value) {
            this.sp.edit().putString("bindHostUrl", value).apply()
        }

}