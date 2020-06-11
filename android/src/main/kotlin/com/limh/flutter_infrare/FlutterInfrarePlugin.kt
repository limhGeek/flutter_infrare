package com.limh.flutter_infrare

import android.content.Context
import android.hardware.ConsumerIrManager
import androidx.annotation.NonNull
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
                AudioUtils.getInstance().play(NecPattern.buildPattern(0X08, 0XE6, data).toList())
            }
        } else {
            result.notImplemented()
        }
//        {"userCode1":8,"userCode2":230,"data":65}
//        8,userH=00010000  230,userL=01100111
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
