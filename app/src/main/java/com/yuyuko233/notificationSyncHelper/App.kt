package com.yuyuko233.notificationSyncHelper

import android.app.Application
import android.content.Context
import com.yuyuko233.notificationSyncHelper.utils.Config

class App : Application() {
    companion object{
        lateinit var context: Context
        lateinit var config: Config
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        config = Config(context)
    }
}