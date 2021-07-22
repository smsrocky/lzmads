package cn.lzm.ads.mintegral

import android.content.Context
import cn.lzm.ads.mintegral.provider.MintegralProvider
import com.ifmvo.togetherad.core.TogetherAd
import com.ifmvo.togetherad.core.entity.AdProviderEntity
import com.mbridge.msdk.MBridgeSDK
import com.mbridge.msdk.out.MBridgeSDKFactory
import org.jetbrains.annotations.NotNull


object TogetherAdMintegral {
    var idMapMintegral = mutableMapOf<String, String>()
    // 可选参数，需在初始化之前，是否打开debug调试信息输出：true打开、false关闭。默认false关闭
    var debug: Boolean = false

    // 可选参数，需在初始化之前，是否打开debug调试信息输出：true打开、false关闭。默认false关闭
    var showNotification: Boolean = false

    fun init(@NotNull context: Context,
             @NotNull adProviderType: String,
             @NotNull mintegralAdAppId: String,
             @NotNull mintegralAdAppKey: String) {
        init(context, adProviderType, mintegralAdAppId, mintegralAdAppKey,null, null)
    }

    fun init(
        @NotNull context: Context,
        @NotNull adProviderType: String,
        @NotNull mintegralAdAppId: String,
        @NotNull mintegralAdAppKey:String,
        providerClassPath: String? = null
    ) {
        init(context, adProviderType, mintegralAdAppId, mintegralAdAppKey,null, providerClassPath)
    }

    fun init(
        @NotNull context: Context,
        @NotNull adProviderType: String,
        @NotNull mintegralAdAppId: String,
        @NotNull mintegralAdAppKey:String,
        mintegralIdMap: Map<String, String>? = null
    ) {
        init(context, adProviderType, mintegralAdAppId,mintegralAdAppKey, mintegralIdMap, null)
    }

    fun init(
        @NotNull context: Context,
        @NotNull adProviderType: String,
        @NotNull mintegralAdAppId: String,
        @NotNull mintegralAdAppKey: String,
        mintegralIdMap: Map<String, String>? = null,
        providerClassPath: String? = null
    ) {
        TogetherAd.addProvider(AdProviderEntity(adProviderType, if (providerClassPath?.isEmpty() != false) MintegralProvider::class.java.name else providerClassPath))
        mintegralIdMap?.let { idMapMintegral.putAll(it) }
        val sdk: MBridgeSDK = MBridgeSDKFactory.getMBridgeSDK()
        val map = sdk.getMBConfigurationMap(mintegralAdAppId, mintegralAdAppKey)
        sdk.init(map, context)
    }
}