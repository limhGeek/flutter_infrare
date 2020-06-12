package com.limh.flutter_infrare

import android.content.Context
import android.hardware.ConsumerIrManager
import android.media.AudioManager
import android.media.AudioManager.GET_DEVICES_OUTPUTS
import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import io.flutter.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import org.json.JSONObject

/** FlutterInfrarePlugin */
public class FlutterInfrarePlugin : FlutterPlugin, MethodCallHandler {
    private val TAG = "flutter_infrare"
    private lateinit var channel: MethodChannel
    private lateinit var service: ConsumerIrManager
    private var am: AudioManager? = null
    private lateinit var context: Context

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        service = flutterPluginBinding.applicationContext.getSystemService(Context.CONSUMER_IR_SERVICE) as ConsumerIrManager
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_infrare")
        channel.setMethodCallHandler(this);
    }

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "flutter_infrare")
            channel.setMethodCallHandler(FlutterInfrarePlugin())
        }
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method == "isSupport") {
            val isSupport = service.hasIrEmitter()
            if (isSupport) {
                val range = service.carrierFrequencies
                range.forEach {
                    Log.d(TAG, "max:${it.maxFrequency},min:${it.minFrequency}")
                }
            }
            return result.success(isSupport)
        } else if (call.method == "send") {
            val params: String = call.arguments as String
            val paramsJson = JSONObject(params)
            Log.d(TAG, "收到数据：$params")
            val userCodeH = paramsJson.getInt("userCode1")
            val userCodeL = paramsJson.getInt("userCode2")
            val data = paramsJson.getInt("data")
            Log.d(TAG, "send data=$data")
            if (service.hasIrEmitter()) {
                service.transmit(38000, NecPattern.buildPattern(userCodeH, userCodeL, data))
            } else {
                Log.d(TAG, "不支持硬件红外模块")
                if (null == am) {
                    am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                }
                var canOut = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val outputs = am?.getDevices(GET_DEVICES_OUTPUTS)
                    if (null == outputs || outputs.isEmpty()) {
                        canOut = false
                    }
                } else {
                    am?.let {
                        canOut = it.isWiredHeadsetOn
                    }
                }
                if (canOut) {
                    AudioUtils.getInstance().play(NecPattern.buildPattern(userCodeH, userCodeL, data).toList())
                } else {
                    result.error("-1", "不支持", "手机不支持红外且未接外部模块")
                }
            }
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
