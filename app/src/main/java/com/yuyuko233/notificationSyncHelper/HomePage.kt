package com.yuyuko233.notificationSyncHelper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.maning.mlkitscanner.scan.MNScanManager
import com.yuyuko233.notificationSyncHelper.databinding.HomePageBinding

class HomePage : Fragment() {

    private var _binding: HomePageBinding? = null

    // 仅在创建 view 到 销毁 view 生命周期之间有效
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomePageBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        // 初始化各类值
        // 服务状态
        binding.servicesStatus.isChecked = App.config.serviceStatus
        // 绑定主机
        binding.hostUrl.setText(App.config.bindHostUrl)
        // 调试模式
        binding.debugStatus.isChecked = App.config.debugMode

        // 绑定事件监听
        // 切换服务状态
        binding.servicesStatus.setOnCheckedChangeListener { _, state -> App.config.serviceStatus = state }
        // 切换调试模式
        binding.debugStatus.setOnCheckedChangeListener { _, state -> App.config.debugMode = state }

        // 绑定主机链接
        binding.setBindUrl.setOnClickListener {
            App.config.bindHostUrl = binding.hostUrl.text.toString()
            binding.hostUrl.clearFocus()
        }
        // 扫描二维码
        binding.scanQrCode.setOnClickListener {
            MNScanManager.startScan(this.activity) { resultCode, data ->
                when (resultCode) {
                    MNScanManager.RESULT_SUCCESS -> {
                        val text = data.getStringArrayListExtra(MNScanManager.INTENT_KEY_RESULT_SUCCESS)?.get(0).toString()
                        binding.hostUrl.setText(text)
                    }
                    MNScanManager.RESULT_FAIL ->
                        Snackbar.make(
                            view,
                            "错误: \n${data.getStringExtra(MNScanManager.INTENT_KEY_RESULT_ERROR)}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    MNScanManager.RESULT_CANCLE ->
                        Snackbar.make(view, "取消扫码", Snackbar.LENGTH_SHORT)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}