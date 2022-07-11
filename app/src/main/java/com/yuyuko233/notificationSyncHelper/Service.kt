package com.yuyuko233.notificationSyncHelper

import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.FormBody
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.util.concurrent.TimeUnit

private val client = OkHttpClient.Builder()
    .connectTimeout(3, TimeUnit.SECONDS)
    .writeTimeout(2, TimeUnit.SECONDS)
    .readTimeout(2, TimeUnit.SECONDS)
    .build()

class Service : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // 基础过滤
        if (!App.config.serviceStatus || event.packageName == "android") return
        if (event.eventType != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED || event.parcelableData == null) return
        // 获取通知对象并取标题, 内容
        val notification: Notification = event.parcelableData as Notification
        val title: String? = notification.extras.getString(Notification.EXTRA_TITLE)
        var text: String? = notification.extras.getString(Notification.EXTRA_TEXT)
        // 过滤全空通知
        if (title == null && text == null) return
        if (text == null) {
            text = notification.extras.get(Notification.EXTRA_TEXT).toString()
        }
        // 调试模式下输出
        if (App.config.debugMode) {
            Toast.makeText(applicationContext, "${event.packageName}\n${title.toString()}\n${text}", Toast.LENGTH_LONG).show()
        }
        // 推送到主机
        val request = Request.Builder()
            .url(App.config.bindHostUrl)
            .post(
                FormBody.Builder()
                .add("title", title.toString())
                .add("text", text)
                .build()
            ).build()
        // 异步回调
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        return
                    }
                    Log.e("OkHttpResponseErr", response.toString())
                    Looper.prepare()
                    Toast.makeText(
                        applicationContext,
                        "${getString(R.string.SyncNotificationErr)}\n$response",
                        Toast.LENGTH_LONG
                    ).show()
                    Looper.loop()
                }
            }

            override fun onFailure(call: Call, e: java.io.IOException) {
                Log.e("OkHttpOnFailure", e.toString())
                Looper.prepare()
                Toast.makeText(applicationContext, "${getString(R.string.AppName)} \n $e", Toast.LENGTH_LONG).show()
                Looper.loop()
            }
        })
    }

    override fun onInterrupt() {
        Toast.makeText(applicationContext, "无障碍服务已停止 !", Toast.LENGTH_SHORT).show()
    }
}