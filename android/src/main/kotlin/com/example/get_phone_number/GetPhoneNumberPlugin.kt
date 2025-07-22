package com.example.get_phone_number

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class GetPhoneNumberPlugin : FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware {
    private lateinit var channel: MethodChannel
    private var activity: Activity? = null
    private lateinit var context: Context

    companion object {
        private const val CHANNEL_NAME = "get_phone_number"
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(binding.binaryMessenger, CHANNEL_NAME)
        channel.setMethodCallHandler(this)
        context = binding.applicationContext
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        if (call.method == "getPhoneNumber") {
            val ctx = activity ?: context

            val hasPermission =
                ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                result.error("PERMISSION_DENIED", "Permissões READ_PHONE_NUMBERS ou READ_PHONE_STATE não concedidas", null)
                return
            }

            try {
                val subscriptionManager = ctx.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

                // ✅ API moderna do Android 13+ (Tiramisu)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val infoList = subscriptionManager.activeSubscriptionInfoList
                    if (!infoList.isNullOrEmpty()) {
                        val subId = infoList[0].subscriptionId
                        val number = subscriptionManager.getPhoneNumber(subId)
                        if (!number.isNullOrEmpty()) {
                            result.success(number)
                            return
                        }
                    }
                }

                // ✅ Fallback para Android 5.1+ (Lollipop MR1)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    val infoList = subscriptionManager.activeSubscriptionInfoList
                    val firstNumber = infoList?.firstOrNull()?.number
                    if (!firstNumber.isNullOrEmpty()) {
                        result.success(firstNumber)
                        return
                    }

                    // Vários números, se disponíveis
                    val allNumbers = infoList?.mapNotNull { it.number?.takeIf { num -> num.isNotEmpty() } }
                    if (!allNumbers.isNullOrEmpty()) {
                        result.success(allNumbers.joinToString("\n"))
                        return
                    }
                }

                // ✅ Último fallback: line1Number
                val tm = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                result.success(tm.line1Number ?: "")
            } catch (e: Exception) {
                result.error("EXCEPTION", "Erro ao acessar número: ${e.message}", null)
            }
        } else {
            result.notImplemented()
        }
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        activity = null
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
