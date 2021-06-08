package cn.lzm.ads.ks

import android.content.Context
import cn.lzm.ads.ks.povider.KsProvider
import com.ifmvo.togetherad.core.TogetherAd
import com.ifmvo.togetherad.core.entity.AdProviderEntity
import com.kwad.sdk.api.KsAdSDK
import com.kwad.sdk.api.SdkConfig
import org.jetbrains.annotations.NotNull

/**
 * 第三方广告平台
 * 快看初始化
 *
 */
object TogetherAdKs {
    var idMapKs = mutableMapOf<String, String>()
    // 可选参数，需在初始化之前，是否打开debug调试信息输出：true打开、false关闭。默认false关闭
    var debug: Boolean = false

    // 可选参数，需在初始化之前，是否打开debug调试信息输出：true打开、false关闭。默认false关闭
    var showNotification: Boolean = false

    fun init(@NotNull context: Context, @NotNull adProviderType: String, @NotNull ksAdAppId: String) {
        init(context, adProviderType, ksAdAppId, null, null)
    }

    fun init(
        @NotNull context: Context,
        @NotNull adProviderType: String,
        @NotNull ksAdAppId: String,
        providerClassPath: String? = null
    ) {
        init(context, adProviderType, ksAdAppId, null, providerClassPath)
    }

    fun init(
        @NotNull context: Context,
        @NotNull adProviderType: String,
        @NotNull ksAdAppId: String,
        ksIdMap: Map<String, String>? = null
    ) {
        init(context, adProviderType, ksAdAppId, ksIdMap, null)
    }

    fun init(
        @NotNull context: Context,
        @NotNull adProviderType: String,
        @NotNull ksAdAppId: String,
        ksIdMap: Map<String, String>? = null,
        providerClassPath: String? = null
    ) {
        TogetherAd.addProvider(AdProviderEntity(adProviderType, if (providerClassPath?.isEmpty() != false) KsProvider::class.java.name else providerClassPath))
        ksIdMap?.let { idMapKs.putAll(it) }
        KsAdSDK.init(context, SdkConfig.Builder()
                .appId(ksAdAppId) // 测试aapId，请联系快手平台申请正式AppId，必填
                .showNotification(showNotification) // 是否展示下载通知栏
                .debug(debug)
                .build()
        )
    }
}